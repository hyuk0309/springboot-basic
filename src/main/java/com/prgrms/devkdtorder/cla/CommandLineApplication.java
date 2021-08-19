package com.prgrms.devkdtorder.cla;

import com.prgrms.devkdtorder.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandLineApplication implements Runnable {

    private final Input input;
    private final Output output;
    private final VoucherService voucherService;

    public CommandLineApplication(Input input, Output output, VoucherService voucherService) {
        this.input = input;
        this.output = output;
        this.voucherService = voucherService;
    }

    @Override
    public void run() {

        while (true) {
            output.printAppStart();
            String command = input.getCommand().toUpperCase();
            if (!validateCommand(command)){
                output.printCommandError();
                continue;
            }

            CommandType commandType = CommandType.valueOf(command);
            if (commandType.equals(CommandType.EXIT)){
                break;
            }


            String result = executeCommand(commandType);
            if (!result.isEmpty()){
                output.print(result);
                continue;
            }

        }



    }

    private String executeCommand(CommandType commandType) {
        String result = "";
        switch (commandType) {
            case CREATE:
                result = createVoucher();
                break;
            case LIST:
                showVouchers();
                break;
        }
        return result;
    }

    private String createVoucher(){
        List<String> list = VoucherType.voucherTypeNames();
        output.print(list);

        String type = input.getVoucherType();
        Optional<VoucherType> voucherType = VoucherType.findByNameOrNo(type);
        if (voucherType.isEmpty()){
            return "올바르지 않은 바우처 입니다.";
        }
        long value = input.getVoucherValue();

        Voucher voucher = VoucherFactory.create(voucherType.get(), value);
        voucherService.saveVoucher(voucher);
        return "";
    }

    private void showVouchers() {
        List<Voucher> vouchers = voucherService.getAllVouchers();
        List<String> metas = vouchers.stream()
                .map(Voucher::toString)
                .collect(Collectors.toList());
        output.print(metas);

    }

    private boolean validateCommand(String command) {
        return CommandType.anyMatch(command);
    }
}