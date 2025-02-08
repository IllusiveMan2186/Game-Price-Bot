package com.gpb.game.util.store;

public class GamazeyConstants {

    public static final String GAME_PAGE_NAME_FIELD = "rm-product-title order-1 order-md-0";
    public static final String GAME_IN_LIST = "rm-module-title";
    public static final String GAME_PAGE_OLD_PRICE_FIELD = "rm-product-center-price-old";
    public static final String GAME_PAGE_DISCOUNT_PRICE_FIELD = "rm-product-center-price";
    public static final String GAME_PAGE_DISCOUNT_FIELD = "main-product-you-save";
    public static final String GAME_PAGE_IS_AVAILABLE = "rm-module-stock rm-out-of-stock";
    public static final String GAME_PAGE_CHARACTERISTICS = "rm-product-attr-list-item d-flex d-sm-block";
    public static final String GAME_IMG_CLASS = "img-fluid";
    public static final String GAMEZEY_SEARCH_URL = "https://gamazey.com.ua/search?search=";
    public static final String GAME_NAME_PRODUCT_TYPE_PART = "^(Гра|Ігрова валюта|Доповнення) ";
    public static final String GAME_NAME_SPECIFICATION_PART = " для .+ \\(Ключ активації .+\\)| " +
            "\\(Ключ активації .+\\)| (Ігрова валюта)";
    public static final String GAME_NAME_PROBLEMATIC_SYMBOLS = "[:|&+]";
    public static final String GAME_GENRES_FIELD = "Жанр";
    public static final int GAME_IMAGE_CROP_WIDTH_START = 20;
    public static final int GAME_IMAGE_CROP_WIDTH_LONG = 320;
    public static final int GAME_IMAGE_HEIGHT = 400;

    private GamazeyConstants() {
    }
}
