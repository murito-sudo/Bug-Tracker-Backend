package com.example.Bug.Tracker.Backend.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class RoleValidator implements ConstraintValidator<ValidRole, String> {
    private final List<String> validRoles = Arrays.asList("User", "Guest", "Admin", "Developer", "Mod");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // @NotNull will handle null check
        }
        return validRoles.contains(value);
    }
}