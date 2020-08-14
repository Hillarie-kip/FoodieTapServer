package com.techkip.foodietapserver.model;

import java.util.List;

public class MyResponse {

    private long multicast_id;
    public int success,failure,canonical_ids;

    public List<Result> resultList;
}
