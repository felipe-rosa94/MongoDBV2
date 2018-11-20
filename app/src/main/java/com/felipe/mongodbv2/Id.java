package com.felipe.mongodbv2;

import com.google.gson.annotations.SerializedName;

public class Id {
    @SerializedName("$oid")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
