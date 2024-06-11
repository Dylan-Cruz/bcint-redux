# bcint-redux
Repo to house a custom domain integration project for Dragon's Lair LLC built on Spring Boot. The job was to enable the importing of rich product listings to Big Commerce which is an e-commerce marketplace provider and maintain their prices based on fluctuations in the market. The bulk of the heavy lifting is in the Tasks sub directory which contains scheduled jobs to perform various tasks. 

## General Workflow
1. Call Scryfall.com apis to gather information about roughly 200,000 individual products each with variants
2. Parse the data and build a BigCommerce product listing with a price calculation
3. Parse output from a Roca Sorter (card scanning robot) and import what was scanned into BigCommerce inventory
4. Manage pricing settings for each product listing
5. Handle errors should they occur by sweeping up corrupted product listings and remaking them

## Tasks
- **ListProductsTask:** Scans the scryfall api for unique Magic: The Gathering products that don't exist on Dragon's Lair's instance of Big Commerge and creates a product listing for them.
- **UpdatePricesTask:** Scans the scryfall api for price fluctuations in Magic: The Gathering card costs and if the price has deviated from the listing price in the Big Commerge database, update it. Pricing is fully configurable via external config.
- **ProcessRocaInputTask:** parses the output from the Roca Sorter which is a card sorting robot and increments the inventory of those products on Big Commerce
- **CleanErrorerListings:** in the event a listing is incomplete or is otherwise in an unrecoverable state, delete it so it will be recreated by the ListProductsTask
