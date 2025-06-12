package com.ylli.shared.enums;

import lombok.Getter;

@Getter
public enum LoanType {
    PERSONAL_LOAN("Personal Loan"),
    AUTO_LOAN("Auto Loan"),
    MORTGAGE("Mortgage"),
    STUDENT_LOAN("Student Loan"),
    BUSINESS_LOAN("Business Loan"),
    HOME_EQUITY_LOAN("Home Equity Loan"),
    OTHER("Other Loan");

    private final String displayName;

    LoanType(String displayName) {
        this.displayName = displayName;
    }
}
