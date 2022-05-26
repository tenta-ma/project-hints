package org.tantama.anchoco;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * test用のリクエストdto
 */
@Getter
@Setter
public class TestHttpRequestDto implements Serializable {

    /** シリアルID */
    private static final long serialVersionUID = 1L;

    /** id */
    private Integer id;

    /** name */
    private String name;

}
