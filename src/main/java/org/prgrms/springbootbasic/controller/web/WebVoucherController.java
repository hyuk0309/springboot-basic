package org.prgrms.springbootbasic.controller.web;

import java.util.UUID;
import org.prgrms.springbootbasic.dto.CreateVoucherRequest;
import org.prgrms.springbootbasic.service.VoucherService;
import org.prgrms.springbootbasic.util.DtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vouchers")
public class WebVoucherController {

    private static final Logger logger = LoggerFactory.getLogger(WebVoucherController.class);
    private final VoucherService voucherService;

    public WebVoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public String viewVouchersPage(Model model) {
        var vouchers = voucherService.findAll();
        model.addAttribute("voucherDTOs", DtoConverter.toVoucherDTOs(vouchers));
        return "voucher/vouchers";
    }

    @GetMapping("/{voucherId}")
    public String viewVoucherPage(@PathVariable("voucherId") UUID voucherId, Model model) {
        var voucher = voucherService.findVoucher(voucherId);
        model.addAttribute("voucherDTO", DtoConverter.toVoucherDTO(voucher));
        return "voucher/voucher";
    }

    @GetMapping("/new")
    public String viewNewVoucherPage(Model model) {
        model.addAttribute("createVoucherRequest", new CreateVoucherRequest());
        return "voucher/new-voucher";
    }

    @PostMapping("/new")
    public String createVoucher(
        @ModelAttribute CreateVoucherRequest createVoucherRequest,
        BindingResult bindingResult) {
        logger.info("Got createVoucherRequest -> {}", createVoucherRequest);

        if (createVoucherRequest.getVoucherType().isFixed()) {
            validateFixedVoucherInput(createVoucherRequest, bindingResult);
        }
        if (createVoucherRequest.getVoucherType().isPercent()) {
            validatePercentVoucherInput(createVoucherRequest, bindingResult);
        }

        if (bindingResult.hasErrors()) {
            logger.info("errors={}", bindingResult);
            return "voucher/new-voucher";
        }

        voucherService.createVoucher(
            createVoucherRequest.getVoucherType(),
            createVoucherRequest.getAmount(),
            createVoucherRequest.getPercent());
        return "redirect:/vouchers";
    }

    @PostMapping("/{voucherId}/delete")
    public String deleteVoucher(@PathVariable("voucherId") UUID voucherId) {
        voucherService.deleteVoucher(voucherId);
        return "redirect:/vouchers";
    }

    private void validatePercentVoucherInput(CreateVoucherRequest createVoucherRequest,
        BindingResult bindingResult) {
        if (createVoucherRequest.getPercent() == 0) {
            bindingResult.rejectValue("percent", "required");
        }
        if (createVoucherRequest.getAmount() != 0) {
            bindingResult.rejectValue("amount", "notRequired");
        }
    }

    private void validateFixedVoucherInput(CreateVoucherRequest createVoucherRequest,
        BindingResult bindingResult) {
        if (createVoucherRequest.getAmount() == 0) {
            bindingResult.rejectValue("amount", "required");
        }
        if (createVoucherRequest.getPercent() != 0) {
            bindingResult.rejectValue("percent", "notRequired");
        }
    }
}
