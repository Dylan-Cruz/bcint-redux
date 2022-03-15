package com.dragonslair.bcintredux;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
import com.dragonslair.bcintredux.scryfall.ScryfallService;
import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
import com.dragonslair.bcintredux.services.MtgAutomationService;
import com.dragonslair.bcintredux.tasks.ProcessRocaInputTask;
import com.dragonslair.bcintredux.tasks.UpdatePricesTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@SpringBootTest
public class SandboxTests {

    @Autowired
    private MtgAutomationService automationService;

    @Autowired
    private BigCommerceService bigCommerceService;

    @Autowired
    private ProcessRocaInputTask inputTask;

    @Autowired
    private UpdatePricesTask updatePricesTask;

    @Autowired
    private ScryfallService scryfallService;

    @Test
    public void testSearchCards() {
        MultiValueMap<String, String> args = new LinkedMultiValueMap<>();
        args.add("q", "e:war");
        List<ScryfallCard> cards = scryfallService.searchCards(args);
        System.out.println(cards.size());
    }

}
