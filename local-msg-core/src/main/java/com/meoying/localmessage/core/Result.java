package com.meoying.localmessage.core;

public interface Result<T> {

    String DEFAULT_ERROR_CODE = "-1";

    String SUCCESS_CODE = "0";
    //  操作成功，但是存在其他问题
    String SUCCESS_CODE_PART = "1";
    String TIMEOUT_CODE = "999";

    static boolean IsTimeout(Result<?> result) {
        return TIMEOUT_CODE.equals(result.getCode());
    }

    static <T> Result<T> Success(String msg, T t) {
        return new SuccessResult<>(msg, t);
    }

    static <T> Result<T> Success(String code, String msg, T t) {
        return new SuccessResult<>(code, msg, t);
    }

    static <T> Result<T> Fail(String code, String msg) {
        return new ErrorResult<>(code, msg);
    }

    boolean isSuccess();

    String getCode();

    String getMsg();

    T getData();

    class ErrorResult<T> implements Result<T> {

        String code;
        String msg;

        public ErrorResult(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getMsg() {
            return msg;
        }

        @Override
        public T getData() {
            return null;
        }

    }

    class SuccessResult<T> implements Result<T> {

        String code;
        String msg;
        T t;

        public SuccessResult(String msg, T t) {
            this.msg = msg;
            this.t = t;
            this.code = SUCCESS_CODE;
        }

        public SuccessResult(String code, String msg, T t) {
            this.code = code;
            this.msg = msg;
            this.t = t;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getMsg() {
            return msg;
        }

        @Override
        public T getData() {
            return t;
        }

    }
}
