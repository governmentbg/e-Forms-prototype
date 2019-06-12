package bg.bulsi.eforms.context.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import bg.bulsi.eforms.model.epayment.Eserviceadminuser;
import bg.bulsi.eforms.model.epayment.VwDepartmentAisClients;
import bg.bulsi.eforms.repository.epayment.EserviceadminuserRepository;
import bg.bulsi.eforms.repository.epayment.VwDepartmentAisClientsRepository;

@Component
public class ApplicationLoaderListener implements ApplicationListener<ContextRefreshedEvent> {

	private EserviceadminuserRepository eserviceRepository;
	private VwDepartmentAisClientsRepository vwDepartmentAisClientsRepository;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		if (context.getParent() != null) {
			this.eserviceRepository = context.getBean(EserviceadminuserRepository.class);
			long count = eserviceRepository.count();
			if (count == 0) {
				initializeDatabaseWithMockData(context);
			}

			/*List<Eserviceadminuser> users = eserviceRepository.findAll();
			for (Eserviceadminuser user : users) {
				System.out.println(user.getUsername());
				for (VwDepartmentAisClients dep : user.getDepartments()) {
					System.out.println(dep.getDepartmentname() + " - " + dep.getAisname());
				}
			}*/

		}
	}

	private void initializeDatabaseWithMockData(ApplicationContext context) {
		Eserviceadminuser user = new Eserviceadminuser();
		user.setEserviceadminuserid(1);
		user.setUsername("emil");
		user.setPasswordhash("AAqSAUis7KLsLOUcONqiLFe4sinSlpQaiuQeA5UN4A6Syj0PD+qCcTzcKXZfkagQuA==");
		user.setPasswordsalt("xPaxnMA6niM6plvFlFgJ9Q==");
		user.setName("Емил Дечев Денчовски");
		user.setDepartmentid("1");
		user.setIsactive(true);

		eserviceRepository.save(user);

		this.vwDepartmentAisClientsRepository = context.getBean(VwDepartmentAisClientsRepository.class);
		VwDepartmentAisClients department1 = new VwDepartmentAisClients();
		department1.setDepartmentid(1);
		department1.setDepartmentname("НОИ");
		department1.setAisname("ИС гр. Ловеч");
		department1.setClientid("testAisClient");
		department1.setSecretkey(
				"8F70C29ACBB38F39B0900C26B3A20B0683E62C97A4E578358904686D0023988D0C9873AD23EE87003B36EE5221617FEC0345E3B1138FE1B57EF5DE4771E3CF42");
		department1.setServiceprovideriban("BG80BNBG96611020345678");
		department1.setServiceproviderbic("BNBGBGSF");
		department1.setServiceproviderbank("БНБ");

		VwDepartmentAisClients department2 = new VwDepartmentAisClients();
		department2.setDepartmentid(1);
		department2.setDepartmentname("НОИ");
		department2.setAisname("ИС гр. Трън");
		department2.setClientid("testBankClient");
		department2.setSecretkey(
				"4E78C4A8564B9204B9D7A06DD799A286F6260742F72700F77631FCCBC0C9497F3E2FF1F6B3DFD5EA7E2224EDCCCE37889B0D9F3AEF5F7E4E69CA7E20675BD255");
		department2.setServiceprovideriban("BG80BNBG96611020345678");
		department2.setServiceproviderbic("BNBGBGSF");
		department2.setServiceproviderbank("Българска народна банка");

		vwDepartmentAisClientsRepository.save(department1);
		vwDepartmentAisClientsRepository.save(department2);
	}
}