package si.kozelj.webcrawler.repositories;

import org.springframework.data.repository.CrudRepository;
import si.kozelj.webcrawler.models.DataType;

public interface DataTypeRepository extends CrudRepository<DataType, String> {

    DataType getByCode(String code);

    default DataType getPdfType() {
        return getByCode("PDF");
    }

    default DataType getDocType() {
        return getByCode("DOC");
    }

    default DataType getDocxType() {
        return getByCode("DOCX");
    }

    default DataType getPptType() {
        return getByCode("PPT");
    }

    default DataType getPptxType() {
        return getByCode("PPTX");
    }
}
