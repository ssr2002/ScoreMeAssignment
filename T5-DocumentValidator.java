public class DocumentValidator {

    

    private static final Logger logger = LoggerFactory.getLogger(DocumentValidator.class);

    public ValidationResult validate(Document doc) {
        try {

            // FIX 1: Do NOT throw generic RuntimeException for expected validation failures.
            // These are business validation cases, not system errors.
            // Return a proper ValidationResult instead of throwing.
            if (doc == null) {
                logger.warn("Validation failed: Document is null"); // expected case → WARN, not ERROR
                return ValidationResult.invalid("Document is null");
            }

            String content = doc.extractContent();

            if (content == null || content.isEmpty()) {
                // FIX 2: Handle expected validation failure without exception spam
                logger.warn("Validation failed: Empty content");
                return ValidationResult.invalid("Empty content");
            }

            return runValidationRules(content);

        } catch (Exception e) {

            // FIX 3: Replace printStackTrace with structured logging.
            // Only unexpected errors should be logged as ERROR with stack trace.
            logger.error("Unexpected error during document validation", e);

            // FIX 4: Do NOT return null — it causes downstream NPEs.
            // Return a safe failure object instead.
            return ValidationResult.invalid("Internal validation error");
        }
    }

    public void validateBatch(List<Document> docs) {

        for (Document doc : docs) {
            try {
                ValidationResult r = validate(doc);

                // FIX 5: Prevent NullPointerException by ensuring result is checked
                // (defensive check even though validate() now never returns null)
                if (r != null && r.isValid()) {
                    saveResult(r);
                }

            } catch (Exception e) {

                // FIX 6: Do NOT swallow exceptions silently.
                // Log them properly so real issues are traceable.
                logger.error("Error processing document in batch", e);
            }
        }
    }
}
