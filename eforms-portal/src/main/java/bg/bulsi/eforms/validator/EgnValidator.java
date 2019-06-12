package bg.bulsi.eforms.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesValidator(value = "egnValidator")
public class EgnValidator implements Validator {

	public EgnValidator() {
		super();
		// TODO Auto-generated constructor stub
	}


	private static Log log = LogFactory.getLog(EGN.class);


	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

		/*
		 * Identification number for Bulgarian person
		 */
		EGN egn = new EGN(value.toString().trim(), null);

		/*
		 * Identification number for Foreign person
		 */
		LNCH lnch = new LNCH(value.toString().trim());

		FacesMessage msg;

		// String clientId = component.getClientId(context);

		// if (egn.isValidEgn()) {
		// msg = new FacesMessage("egn.valid", "egn.valid"); // ("Валидно ЕГН/ЛНЧ"), "Валидно ЕГН/ЛНЧ");
		// msg.setSeverity(FacesMessage.SEVERITY_INFO);
		// context.addMessage(clientId, msg);
		//
		// } else if (lnch.isValidLnch()) {
		// msg = new FacesMessage("lnch.valid", "lnch.valid"); // LNCH: 9000099999
		// msg.setSeverity(FacesMessage.SEVERITY_INFO);
		// context.addMessage(clientId, msg);
		// } else

		if (!egn.isValidEgn() && !lnch.isValidLnch()) {
			msg = new FacesMessage("Невалидно ЕГН", "Невалидно ЕГН");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}


	public class LNCH {

		private String lnch;


		public LNCH(String lnch) {
			this.lnch = lnch;
		}


		public boolean isValidLnch() {
			if ((lnch != null) && isNumber(lnch) && (lnch.length() == 10)) {

				int multiplication = 0, sum = 0, rest = 0;
				int[] weight = { 21, 19, 17, 13, 11, 9, 7, 3, 1 };
				int[] LNCH = new int[10];

				for (int i = 0; i < 10; i++) {
					LNCH[i] = Character.getNumericValue(lnch.charAt(i));
				}

				for (int i = 0; i < 9; i++) {
					multiplication = weight[i] * LNCH[i];
					sum = sum + multiplication;
				}

				rest = sum % 10;

				if (LNCH[9] != rest) {
					return false;
				}

				return true;
			} else {
				return false;
			}
		}
	}

	public class EGN {

		private String egn;
		private String birthDate;

		private int yearBorn;
		private int monthBorn;
		private int dayBorn;


		public EGN(String egn, String birthDate) {
			this.egn = egn;
			this.birthDate = birthDate;
		}


		public boolean isValidEgn() {
			clean();
			if ((egn != null) && isNumber(egn) && (egn.length() == 10)) {
				if (!checkAndSetDateFromEGN()) {
					return false;
				}

				int multiplication = 0, sum = 0, rest = 0;
				int[] weight = { 2, 4, 8, 5, 10, 9, 7, 3, 6 };
				int[] EGN = new int[10];

				for (int i = 0; i < 10; i++) {
					EGN[i] = Character.getNumericValue(egn.charAt(i));
				}

				for (int i = 0; i < 9; i++) {
					multiplication = weight[i] * EGN[i];
					sum = sum + multiplication;
				}

				rest = sum % 11;

				if ((rest == 0) || (rest == 10)) {
					if (EGN[9] != 0) {
						return false;
					}
				} else {
					if (EGN[9] != rest) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}


		public boolean isAdult() {
			clean();
			if (egn != null) {
				if (!checkAndSetDateFromEGN()) {
					return false;
				}
			} else if (birthDate != null) {
				if (!checkAndSetDateFromBirthDate()) {
					return false;
				}
			}

			Calendar currDate = Calendar.getInstance();
			int yearCurr = currDate.get(Calendar.YEAR);
			int monthCurr = currDate.get(Calendar.MONTH) + 1;
			int dayCurr = currDate.get(Calendar.DAY_OF_MONTH);

			int yearDiff = yearCurr - yearBorn;
			int monthDiff = monthCurr - monthBorn;
			int dayDiff = dayCurr - dayBorn;

			if (monthDiff < 0) {
				yearDiff--;
			} else if (monthDiff == 0) {
				if (dayDiff < 0) {
					yearDiff--;
				}
			}

			if (yearDiff >= 18) {
				return true;
			} else {
				return false;
			}
		}


		private boolean checkAndSetDateFromBirthDate() {
			Calendar birthdate = Calendar.getInstance();
			String pattern = "dd.MM.yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			try {
				birthdate.setTimeInMillis(sdf.parse(birthDate).getTime());
			} catch (ParseException e) {
				log.error("Date pattern for date must be: " + pattern);
				return false;
			}

			dayBorn = birthdate.get(Calendar.DAY_OF_MONTH);
			monthBorn = birthdate.get(Calendar.MONTH) + 1;
			yearBorn = birthdate.get(Calendar.YEAR);

			return compareDateWithCurrDate();
		}


		private boolean checkAndSetDateFromEGN() {
			if (!egn.matches("[0-9]{10}")) {
				return false;
			}
			int yearEgn = Integer.parseInt(egn.substring(0, 2));
			int monthEgn = Integer.parseInt(egn.substring(2, 4));
			dayBorn = Integer.parseInt(egn.substring(4, 6));

			if (monthEgn <= 12) {
				yearBorn = yearBorn + yearEgn;
				monthBorn = monthEgn;
			} else if ((monthEgn > 20) && (monthEgn <= 32)) {
				yearBorn = (yearBorn - 100) + yearEgn;
				monthBorn = monthEgn - 20;
			} else if ((monthEgn > 40) && (monthEgn <= 52)) {
				yearBorn = (yearBorn + 100) + yearEgn;
				monthBorn = monthEgn - 40;
			} else {
				return false;
			}
			return compareDateWithCurrDate();
		}


		private boolean compareDateWithCurrDate() {
			Calendar currDate = Calendar.getInstance();
			currDate.setTimeInMillis(currDate.getTimeInMillis() + 86400000);

			Calendar bornDate = Calendar.getInstance();
			bornDate.set(Calendar.YEAR, yearBorn);
			bornDate.set(Calendar.MONTH, monthBorn - 1);
			bornDate.set(Calendar.DAY_OF_MONTH, dayBorn);

			return currDate.after(bornDate);
		}


		private void clean() {
			yearBorn = 1900;
			monthBorn = 1;
			dayBorn = 1;
		}

	}


	private boolean isNumber(String egn) {
		if (egn.matches("\\d+")) {
			return true;
		} else {
			return false;
		}
	}
}
