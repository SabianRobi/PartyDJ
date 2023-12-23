package partydj.backend.rest.editor;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import partydj.backend.rest.entity.enums.PlatformType;

import java.beans.PropertyEditorSupport;

public class PlatformTypeEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) {
        if (StringUtils.isBlank(text)) {
            setValue(null);
        } else {
            setValue(EnumUtils.getEnum(PlatformType.class, text.toUpperCase()));
        }
    }
}
