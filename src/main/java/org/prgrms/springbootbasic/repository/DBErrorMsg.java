package org.prgrms.springbootbasic.repository;

public class DBErrorMsg {

    public static final String NOTHING_WAS_INSERTED_EXP_MSG = "Nothing was inserted";
    public static final String NOTHING_WAS_DELETED_EXP_MSG = "Nothing was deleted";
    public static final String GOT_EMPTY_RESULT_MSG = "Got empty result";
    public static final String NOTING_WAS_UPDATED_EXP_MSG = "Noting was updated";

    private DBErrorMsg() {
        throw new AssertionError("DBErrorMsg.class는 생성할 수 없습니다.");
    }
}
