package com.example.ragdemo.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Demo tools the agent can call on its own.
 * In a real app, these would hit a database or another service —
 * here we use an in-memory map just to demonstrate the concept.
 */
@Component
public class LeaveTools {

    // employeeId -> remaining casual leaves
    private final Map<String, Integer> leaveBalances = new HashMap<>(Map.of(
            "E101", 8,
            "E102", 3,
            "E103", 12
    ));

    @Tool(description = "Get the remaining casual leave balance for an employee ID")
    public String getLeaveBalance(String employeeId) {
        Integer balance = leaveBalances.get(employeeId);
        if (balance == null) {
            return "No employee found with ID " + employeeId;
        }
        return "Employee " + employeeId + " has " + balance + " casual leaves remaining.";
    }

    @Tool(description = "Apply for leave for an employee on given dates. Deducts 1 day per date from their balance.")
    public String applyLeave(String employeeId, String dates) {
        Integer balance = leaveBalances.get(employeeId);
        if (balance == null) {
            return "No employee found with ID " + employeeId;
        }
        if (balance <= 0) {
            return "Cannot apply — employee " + employeeId + " has no leaves left.";
        }
        leaveBalances.put(employeeId, balance - 1);
        return "Leave applied for employee " + employeeId + " on " + dates
                + ". Remaining balance: " + (balance - 1);
    }
}
