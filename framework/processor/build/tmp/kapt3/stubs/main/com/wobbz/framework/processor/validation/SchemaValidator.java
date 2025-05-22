package com.wobbz.framework.processor.validation;

/**
 * Validates settings.json schema files against the LSPosedKit schema format.
 * This validator is used during compilation by the annotation processor.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u001bB\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J4\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\u00062\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\b2\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J<\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\u00012\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\b2\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J4\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\u00062\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\b2\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J4\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\u00062\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\b2\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J,\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00062\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\b2\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J4\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\u00062\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\b2\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J\u001e\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J\u000e\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\tJ4\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\u00062\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\b2\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002\u00a8\u0006\u001c"}, d2 = {"Lcom/wobbz/framework/processor/validation/SchemaValidator;", "", "()V", "validateAdditionalProperties", "", "jsonObject", "Lorg/json/JSONObject;", "warnings", "", "", "validateArrayProperty", "key", "property", "errors", "validateDefaultValue", "type", "defaultValue", "validateNumericProperty", "validateObjectProperty", "validateProperties", "properties", "validateProperty", "validateRootProperties", "validateSchema", "Lcom/wobbz/framework/processor/validation/SchemaValidator$ValidationResult;", "schemaJson", "validateStringProperty", "ValidationResult", "processor"})
public final class SchemaValidator {
    @org.jetbrains.annotations.NotNull
    public static final com.wobbz.framework.processor.validation.SchemaValidator INSTANCE = null;
    
    private SchemaValidator() {
        super();
    }
    
    /**
     * Validates a settings.json schema string.
     * @param schemaJson The JSON schema string to validate
     * @return ValidationResult with outcome and any errors/warnings
     */
    @org.jetbrains.annotations.NotNull
    public final com.wobbz.framework.processor.validation.SchemaValidator.ValidationResult validateSchema(@org.jetbrains.annotations.NotNull
    java.lang.String schemaJson) {
        return null;
    }
    
    /**
     * Validates the root level properties of the schema.
     */
    private final void validateRootProperties(org.json.JSONObject jsonObject, java.util.List<java.lang.String> errors) {
    }
    
    /**
     * Validates individual properties in the properties section.
     */
    private final void validateProperties(org.json.JSONObject properties, java.util.List<java.lang.String> errors, java.util.List<java.lang.String> warnings) {
    }
    
    /**
     * Validates a single property definition.
     */
    private final void validateProperty(java.lang.String key, org.json.JSONObject property, java.util.List<java.lang.String> errors, java.util.List<java.lang.String> warnings) {
    }
    
    /**
     * Validates default value matches the specified type.
     */
    private final void validateDefaultValue(java.lang.String key, java.lang.String type, java.lang.Object defaultValue, java.util.List<java.lang.String> errors, java.util.List<java.lang.String> warnings) {
    }
    
    /**
     * Validates numeric property constraints.
     */
    private final void validateNumericProperty(java.lang.String key, org.json.JSONObject property, java.util.List<java.lang.String> errors, java.util.List<java.lang.String> warnings) {
    }
    
    /**
     * Validates string property constraints.
     */
    private final void validateStringProperty(java.lang.String key, org.json.JSONObject property, java.util.List<java.lang.String> errors, java.util.List<java.lang.String> warnings) {
    }
    
    /**
     * Validates array property constraints.
     */
    private final void validateArrayProperty(java.lang.String key, org.json.JSONObject property, java.util.List<java.lang.String> errors, java.util.List<java.lang.String> warnings) {
    }
    
    /**
     * Validates object property constraints.
     */
    private final void validateObjectProperty(java.lang.String key, org.json.JSONObject property, java.util.List<java.lang.String> errors, java.util.List<java.lang.String> warnings) {
    }
    
    /**
     * Validates additional properties and checks for deprecated features.
     */
    private final void validateAdditionalProperties(org.json.JSONObject jsonObject, java.util.List<java.lang.String> warnings) {
    }
    
    /**
     * Validation result containing the outcome and any errors found.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0002\u0010\bJ\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\u000f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J3\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00032\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0006H\u00d6\u0001R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u000bR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\n\u00a8\u0006\u0016"}, d2 = {"Lcom/wobbz/framework/processor/validation/SchemaValidator$ValidationResult;", "", "isValid", "", "errors", "", "", "warnings", "(ZLjava/util/List;Ljava/util/List;)V", "getErrors", "()Ljava/util/List;", "()Z", "getWarnings", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "", "toString", "processor"})
    public static final class ValidationResult {
        private final boolean isValid = false;
        @org.jetbrains.annotations.NotNull
        private final java.util.List<java.lang.String> errors = null;
        @org.jetbrains.annotations.NotNull
        private final java.util.List<java.lang.String> warnings = null;
        
        public ValidationResult(boolean isValid, @org.jetbrains.annotations.NotNull
        java.util.List<java.lang.String> errors, @org.jetbrains.annotations.NotNull
        java.util.List<java.lang.String> warnings) {
            super();
        }
        
        public final boolean isValid() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<java.lang.String> getErrors() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<java.lang.String> getWarnings() {
            return null;
        }
        
        public final boolean component1() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<java.lang.String> component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<java.lang.String> component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.wobbz.framework.processor.validation.SchemaValidator.ValidationResult copy(boolean isValid, @org.jetbrains.annotations.NotNull
        java.util.List<java.lang.String> errors, @org.jetbrains.annotations.NotNull
        java.util.List<java.lang.String> warnings) {
            return null;
        }
        
        @java.lang.Override
        public boolean equals(@org.jetbrains.annotations.Nullable
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public java.lang.String toString() {
            return null;
        }
    }
}