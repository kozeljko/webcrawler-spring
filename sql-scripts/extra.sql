ALTER TABLE crawldb.site
ADD CONSTRAINT uniq_site_idx UNIQUE (domain);