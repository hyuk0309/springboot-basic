package org.prgrms.springbootbasic.repository.voucher;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.prgrms.springbootbasic.dto.VoucherDTO;
import org.prgrms.springbootbasic.entity.Customer;
import org.prgrms.springbootbasic.entity.voucher.FixedAmountVoucher;
import org.prgrms.springbootbasic.entity.voucher.PercentDiscountVoucher;
import org.prgrms.springbootbasic.entity.voucher.Voucher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("dev1")
public class FileVoucherRepository implements VoucherRepository {

    private static final Logger logger = LoggerFactory.getLogger(FileVoucherRepository.class);
    private static final String VOUCHER_DB_SER = "filedb/VoucherDB.ser";

    private final File VoucherStorage = new File(VOUCHER_DB_SER);

    @Override
    public void save(Voucher voucher) {
        logger.info("save() called");

        var voucherDTO = new VoucherDTO(voucher);
        try (ObjectOutputStream stream = new ObjectOutputStream(
            new FileOutputStream(VoucherStorage, true))) {
            stream.writeObject(voucherDTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Voucher> findAll() {
        logger.info("findAll() called");

        List<VoucherDTO> voucherDTOs = new ArrayList<>();

        try (FileInputStream VoucherFileStream = new FileInputStream(VoucherStorage)) {
            while (true) {
                ObjectInputStream stream = new ObjectInputStream(VoucherFileStream);
                VoucherDTO voucherDTO = (VoucherDTO) stream.readObject();
                voucherDTOs.add(voucherDTO);
            }
        } catch (EOFException of) {
            logger.info("File read complete");
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return voucherDTOs.stream()
            .map(this::DTOToVoucher)
            .collect(Collectors.toList());
    }

    private Voucher DTOToVoucher(VoucherDTO voucherDTO) {
        if (voucherDTO.getVoucherType().isFixed()) {
            return new FixedAmountVoucher(voucherDTO.getVoucherId(), voucherDTO.getAmount());
        } else {
            return new PercentDiscountVoucher(voucherDTO.getVoucherId(), voucherDTO.getPercent());
        }
    }

    @Override
    public void removeAll() {
        try {
            new FileOutputStream(VoucherStorage).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Voucher updateCustomerId(Voucher voucher) {
        throw new AssertionError("아직 개발 안함");
    }

    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        throw new AssertionError("아직 개발 안함");
    }

    @Override
    public List<Voucher> findByCustomer(Customer customer) {
        throw new AssertionError("아직 개발 안함");
    }

    @Override
    public void deleteVoucher(Voucher voucher) {
        throw new AssertionError("아직 개발 안함");
    }
}
