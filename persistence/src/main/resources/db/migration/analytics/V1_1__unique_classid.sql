ALTER TABLE "objectclass"
ADD CONSTRAINT "classid_unique" UNIQUE ("classid");

INSERT INTO "objectclass"("name", "classid") values('car', '2');