package commons.econ;

/**
 * @author vadim
 */
public enum TransactionResponse {

	/**
	 * A successful transaction.
	 */
	CONFIRMED,

	/**
	 * No result yet.
	 */
//	PENDING,

	/**
	 * Rejected due to lack of balance.
	 */
	REJECTED;

}
