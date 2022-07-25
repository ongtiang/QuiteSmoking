package com.example.quitesmoking;

public class Goal {
    private Long goalDate;

    public Goal(Long goalDate) {
        this.goalDate = goalDate;
    }

    public Goal() {
        //empty constructor needed
    }

    public Long getGoalDate() {
        return goalDate;
    }

    public void setGoalDate(Long goalDate) {
        this.goalDate = goalDate;
    }
}
