package org.prgrms.kdtspringhomework.io;

public interface Output {
    //프로그램 시작시 출력 메시지
    void start();

    //동작 입력 메시지
    void inputTypeMessage();

    //voucher 타입 메시지
    void voucherTypeMessage();

    //금액 입력 메시지
    void inputAmountMessage();

    //성공했을 때 출력 메시지
    void success();

    //프로그램 종료시 출력 메시지
    void exit();

    //오류시 출력 메시지
    void inputError();
}