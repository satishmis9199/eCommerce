import lombok.Data;

@Data
public class BankDetailsDTO {

    private String accountHolderName;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String branchName;

}