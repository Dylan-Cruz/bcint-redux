package com.dragonslair.bcintredux.bigcommerce;

import com.dragonslair.bcintredux.bigcommerce.dto.*;
import com.dragonslair.bcintredux.bigcommerce.rest.BcApiErrorResponse;
import com.dragonslair.bcintredux.bigcommerce.rest.BcApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BigCommerceService {

    private WebClient webClient;

    public BigCommerceService(@Autowired WebClient bigCommerceWebClient) {
        webClient = bigCommerceWebClient;
    }

    /**
     * Returns a big commerce variant by sku
     * @param variantSku
     * @return Variant
     */
    public Variant getVariantBySku(String variantSku) {
        // get our variant
        List<Variant> variants = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/catalog/variants")
                        .queryParam("sku", variantSku)
                        .build()
                )
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                    result.bodyToMono(BcApiErrorResponse.class).flatMap(
                        bcApiErrorResponse -> Mono.error(new BigCommerceServiceException("Error searching for variant with sku "
                            + variantSku
                            + " "
                            + bcApiErrorResponse.toString())
                        )
                    )
                )
                .bodyToMono(new ParameterizedTypeReference<BcApiResponse<List<Variant>>>(){})
                .block()
                .getData();

        // see if we can return one variant
        if (variants.size() == 1) {
            return variants.stream().findFirst().get();
        }

        // we couldn't so throw an error
        if (variants.isEmpty()) {
            throw new BigCommerceServiceException("No variants exist with sku: " + variantSku);
        } else {
            throw new BigCommerceServiceException("More than one variant exists with sku: " + variantSku);
        }
    }

    public Variant updateVariant(int productId, int variantId, String variantSku, Variant patch) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("catalog/products/{productId}/variants/{variantId}")
                        .build(productId, variantId)
                )
                .body(Mono.just(patch), Variant.class)
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                    result.bodyToMono(BcApiErrorResponse.class).flatMap(
                        bcApiErrorResponse -> Mono.error(new BigCommerceServiceException("Error searching for variant with sku "
                            + variantSku
                            + " "
                            + bcApiErrorResponse.toString())
                        )
                    )
                )
                .bodyToMono(new ParameterizedTypeReference<BcApiResponse<Variant>>(){})
                .block()
                .getData();
    }

    public List<Product> getVisibleInStockProductsForCategory(int categoryId) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("categories:in", Integer.toString(categoryId));
        params.add("inventory_level:greater", "0");
        params.add("include", "variants");
        params.add("is_visible", "true");
        params.add("limit", "250");

        return searchProducts(params);
    }

    public List<Product> getProductsForCategoryId(int categoryId) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("categories", Integer.toString(categoryId));
        return searchProducts(params);
    }

    public List<Product> searchProducts(MultiValueMap<String, String> params) {
        return getProducts(uriBuilder -> uriBuilder
                .path("catalog/products")
                .queryParams(params)
                .build()
        ).expand(response -> {
            String next = response.getMeta().getPagination().getLinks().getNext();

            if (next == null) {
                return Mono.empty();
            }
            return getProducts(uriBuilder -> uriBuilder
                    .path("catalog/products")
                    .query(next.substring(1))
                    .build());
        }).flatMap(response -> Flux.fromIterable(response.getData()))
                .collectList()
                .block();
    }

    public Mono<BcApiResponse<List<Product>>> getProducts(Function<UriBuilder, URI> uriFunction) {
        return webClient.get()
                .uri(uriFunction)
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                        result.bodyToMono(BcApiErrorResponse.class).flatMap(
                                bcApiErrorResponse -> Mono.error(new BigCommerceServiceException("Error getting products with uri "
                                        + uriFunction
                                        + " "
                                        + bcApiErrorResponse.toString())
                                )
                        )
                )
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    public List<Metafield> getAllProductMetafields(int productId) {
        return webClient.get()
                .uri(uri -> uri.path("catalog/products/{product_id}/metafields")
                        .build(productId))
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                        result.bodyToMono(BcApiErrorResponse.class).flatMap(
                                bcApiErrorResponse -> Mono.error(new BigCommerceServiceException("Error getting metafields for product with id "
                                        + productId
                                        + " "
                                        + bcApiErrorResponse.toString())
                                )
                        )
                ).bodyToMono(new ParameterizedTypeReference<BcApiResponse<List<Metafield>>>() {
                }).block()
                .getData();
    }

    public Map<String, String> getProductMetafieldMap(int productId) {
        return getAllProductMetafields(productId)
                .stream()
                .collect(Collectors.toMap(Metafield::getKey, Metafield::getValue));
    }

    public List<Category> getSubCategoriesForParent(int categoryId) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("parent_id", Integer.toString(categoryId));
        params.add("limit", "250");
        return searchCategories(params);
    }

    public Category getCategoryByName(String name) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", name);
        return searchCategories(params).stream().findFirst().orElse(null);
    }

    public List<Category> searchCategories(MultiValueMap<String, String> params) {
        return getCategories(uriBuilder -> uriBuilder.path("catalog/categories")
                .queryParams(params)
                .build()
        ).expand(response -> {
            String next = response.getMeta().getPagination().getLinks().getNext();

            if (next == null) {
                return Mono.empty();
            }
            return getCategories((uriBuilder -> uriBuilder
                    .path("catalog/categories")
                    .query(next.substring(1))
                    .build()));
        }).flatMap(response -> Flux.fromIterable(response.getData()))
                .collectList()
                .block();
    }

    public Mono<BcApiResponse<List<Category>>> getCategories(Function<UriBuilder, URI> uriFunction) {
        return webClient.get()
                .uri(uriFunction)
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                        result.bodyToMono(BcApiErrorResponse.class).flatMap(
                                bcApiErrorResponse -> Mono.error(new BigCommerceServiceException("Error getting categories with uri"
                                        + uriFunction
                                        + bcApiErrorResponse.toString())
                                )
                        )
                ).bodyToMono(new ParameterizedTypeReference<BcApiResponse<List<Category>>>() {
                });
    }

    public Category createCategory(Category c) {
        return webClient.post()
                .uri("catalog/categories")
                .body(Mono.just(c), Category.class)
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                        result.bodyToMono(BcApiErrorResponse.class).flatMap(
                                bcApiErrorResponse -> Mono.error(new BigCommerceServiceException("Error creating category "
                                        + c.getName()
                                        + " "
                                        + bcApiErrorResponse.toString())
                                )
                        )
                )
                .bodyToMono(new ParameterizedTypeReference<BcApiResponse<Category>>(){})
                .block()
                .getData();
    }

    public Product createProduct(Product p) {
        return webClient.post()
                .uri("catalog/products")
                .body(Mono.just(p), Product.class)
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                        result.bodyToMono(BcApiErrorResponse.class).flatMap(
                                bcApiErrorResponse -> Mono.error(new BigCommerceServiceException("Error creating product with sku "
                                        + p.getSku()
                                        + " "
                                        + bcApiErrorResponse.toString())
                                )
                        )
                )
                .bodyToMono(new ParameterizedTypeReference<BcApiResponse<Product>>(){})
                .block()
                .getData();
    }

    public Product updateProduct(int productId, Product patch) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder.path("catalog/products/{productId}").build(productId))
                .body(Mono.just(patch), Product.class)
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                        result.bodyToMono(BcApiErrorResponse.class).flatMap(
                                bcApiErrorResponse -> Mono.error(new BigCommerceServiceException("Error updating product with sku "
                                        + patch.getSku()
                                        + " "
                                        + bcApiErrorResponse.toString())
                                )
                        )
                )
                .bodyToMono(new ParameterizedTypeReference<BcApiResponse<Product>>(){})
                .block()
                .getData();
    }

   public void deleteProduct(int productId) {
        webClient.delete()
                .uri(uriBuilder -> uriBuilder.path("catalog/products/{productId}").build(productId))
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                        result.bodyToMono(BcApiErrorResponse.class).flatMap(
                                bcApiErrorResponse -> Mono.error(new BigCommerceServiceException("Error deleting product id "
                                        + productId
                                        + " "
                                        + bcApiErrorResponse.toString())
                                )
                        )
                ).toBodilessEntity().block();
   }

    public ProductImage createProductImage(int productId, CreateProductImage cpi) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("catalog/products/{productId}/images").build(productId))
                .body(Mono.just(cpi), CreateProductImage.class)
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                        result.bodyToMono(BcApiErrorResponse.class).flatMap(
                                bcApiErrorResponse -> Mono.error(new BigCommerceServiceException("Error adding image to product with id "
                                        + productId
                                        + " "
                                        + bcApiErrorResponse.toString())
                                )
                        )
                )
                .bodyToMono(new ParameterizedTypeReference<BcApiResponse<ProductImage>>(){})
                .block()
                .getData();
    }

    public Metafield createProductMetafield(int productId, Metafield metafield) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("catalog/products/{productId}/metafields").build(productId))
                .body(Mono.just(metafield), Metafield.class)
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                        result.bodyToMono(BcApiErrorResponse.class).flatMap(
                                bcApiErrorResponse -> Mono.error(new BigCommerceServiceException("Error adding metafield "
                                        + metafield.getKey()
                                        + " to product with id "
                                        + productId
                                        + " "
                                        + bcApiErrorResponse.toString())
                                )
                        )
                )
                .bodyToMono(new ParameterizedTypeReference<BcApiResponse<Metafield>>(){})
                .block()
                .getData();
    }
}