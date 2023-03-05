package com.doka.customer.service.transaction;

import com.doka.customer.enums.CustomerType;
import com.doka.customer.enums.TransactionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeeCalculationService {

    @Value("${doka.fee.individual.eft}")
    String individualEft;

    @Value("${doka.fee.individual.bill-payment}")
    String individualBillPayment;

    @Value("${doka.fee.business.eft}")
    String businessEft;

    @Value("${doka.fee.business.bill-payment}")
    String businessBillPayment;

    @Value("${doka.fee.business.salary-payment}")
    String businessSalaryPayment;

    public float getRatio(TransactionType transactionType, CustomerType customerType) {
        boolean isIndividual = customerType == CustomerType.INDIVIDUAL;

        String ratio = switch (transactionType) {
            case EFT -> isIndividual ? individualEft : businessEft;
            case BILL_PAYMENT -> isIndividual ? individualBillPayment : businessBillPayment;
            case SALARY_PAYMENT -> businessSalaryPayment;
        };

        return Float.parseFloat(ratio);
    }

}
