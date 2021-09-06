package org.prgrms.kdt.api;

import java.net.URI;
import java.util.Optional;
import org.prgrms.kdt.customer.CustomerDto;
import org.prgrms.kdt.customer.CustomerService;
import org.prgrms.kdt.voucher.VoucherDto;
import org.prgrms.kdt.voucher.VoucherService;
import org.prgrms.kdt.wallet.WalletDto;
import org.prgrms.kdt.wallet.WalletService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yhh1056
 * Date: 2021/09/04 Time: 12:39 오후
 */

@RestController
@RequestMapping(value = Apis.PRE_FIX, produces = MediaType.APPLICATION_JSON_VALUE)
public class Apis {

    protected static final String PRE_FIX = "/kdt/api/v1";
    protected static final String WALLET = "/customer/wallet";
    protected static final String CUSTOMER_ID = "/{customerId}";

    private final CustomerService customerService;
    private final VoucherService voucherService;
    private final WalletService walletService;

    public Apis(CustomerService customerService, VoucherService voucherService, WalletService walletService) {
        this.customerService = customerService;
        this.voucherService = voucherService;
        this.walletService = walletService;
    }

    /**
     * If you have the customer's ID, you can bring about the voucher registered By customer.
     * but returns a 404 if the customer's ID is invalid
     */
    @GetMapping(WALLET + CUSTOMER_ID)
    public ResponseEntity getVouchersByCustomerIid(@PathVariable String customerId) {
        Optional<CustomerDto> customerDto = customerService.getCustomerById(customerId);
        if (customerDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<CustomerDto> walletDto = walletService.getVouchersByCustomer(customerDto);
        if (walletDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(walletDto);
    }

    /**
     * VoucherWalletDto has customer-id and voucher-id.
     * Returns not-found response if the customer and voucher do not exist.
     */
    @PostMapping(WALLET)
    public ResponseEntity insertWallet(@RequestBody WalletDto walletDto) {
        if (isBadRequest(walletDto)) {
            return ResponseEntity.notFound().build();
        }

        walletService.addVoucherByCustomer(walletDto);
        URI uri = URI.create(PRE_FIX + WALLET);
        return ResponseEntity.created(uri).body(walletDto);
    }

    private boolean isBadRequest(WalletDto walletDto) {
        Optional<CustomerDto> customerDto = customerService.getCustomerById(walletDto.getCustomerId());
        if (customerDto.isEmpty()) {
            return true;
        }

        Optional<VoucherDto> voucherDto = voucherService.getVoucherById(walletDto.getVoucherId());
        if (voucherDto.isEmpty()) {
            return true;
        }

        return false;
    }

}