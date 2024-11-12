package com.inswave.whive.common.pricing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PricingService {

    @Autowired
    PricingDaoImpl pricingDao;

    @Transactional
    public void resultPricingInsert(Pricing pricing){

        Pricing pricingInsert = pricing;

        Pricing pricingUpdate = pricingDao.findById(pricingInsert.getUser_id());

        if(pricingUpdate == null){
            pricingDao.insert(pricingInsert);
        }else {

            pricingUpdate.setCustomer_uid(pricingInsert.getCustomer_uid());
            pricingUpdate.setOrder_id(pricingInsert.getOrder_id());
            pricingUpdate.setPay_type_cd(pricingInsert.getPay_type_cd());
            pricingUpdate.setImp_uid(pricingInsert.getImp_uid());
            pricingUpdate.setPay_change_yn(pricing.getPay_change_yn());

            pricingDao.update(pricingUpdate);

        }

    }

    @Transactional
    public  Pricing findById(Long user_id){

        return pricingDao.findById(user_id);

    }


}
