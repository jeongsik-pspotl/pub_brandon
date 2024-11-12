package com.inswave.whive.common.pricing;

public interface PricingDao {

    void insert(Pricing pricing);

    Pricing findById(Long user_id);

    void update(Pricing pricing);

}
