package com.yukisoft.themarket.JavaRepositories;

public class ItemModel {
    private String id;
    private String name;
    private Double price;
    private String details;
    private ItemCondition condition;
    private String userId;
    private String imageUrl;

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    private ItemCategory category;

    /**
     * GETTERS
     */
    public String getUserId() { return userId; }
    public String getImageUrl() { return imageUrl; }
    public String getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public String getDetails() { return details; }
    public ItemCondition getCondition() { return condition; }


    /**
     * SETTERS
     */
    public void setCondition(ItemCondition condition) { this.condition = condition; }
    public void setDetails(String details) { this.details = details; }
    public void setPrice(Double price) { this.price = price; }
    public void setName(String name) { this.name = name; }
    public void setId(String id) { this.id = id; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * EMPTY CONSTRUCTOR
     */
    public ItemModel() { }

    /**
     * MAIN CONSTRUCTOR
     * @param name
     * @param price
     * @param details
     * @param condition
     * @param userId
     * @param imageUrl
     * @param category
     */
    public ItemModel(String name, Double price, String details, ItemCondition condition, String userId, String imageUrl, ItemCategory category) {
        this.name = name;
        this.price = price;
        this.details = details;
        this.condition = condition;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.category = category;
    }
}
