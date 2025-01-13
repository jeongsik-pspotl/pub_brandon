package com.pspotl.sidebranden.common.pricing;

import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;

@Data
public class Pricing {

    private Long pricing_id;
    private Long user_id;
    private String order_id;
    private String customer_uid;
    private String imp_uid;
    private String pay_type_cd;
    private String pay_change_yn;
    private String teamId;
    private String pricing_etc1;
    private String pricing_etc2;
    private LocalDateTime create_date;
    private LocalDateTime update_date;
    private JSONObject pricingObj;

}
