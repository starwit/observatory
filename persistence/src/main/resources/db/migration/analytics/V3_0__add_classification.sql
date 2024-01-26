ALTER TABLE "metadata" ADD COLUMN "classification" VARCHAR(255);
ALTER TABLE "metadata" ADD CONSTRAINT "unique_name_classification" UNIQUE ("name", "classification");