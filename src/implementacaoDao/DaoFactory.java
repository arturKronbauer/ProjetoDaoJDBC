package implementacaoDao;

import dao.DepartamentoDao;
import dao.VendedorDao;
import db.DB;

public class DaoFactory {
	public static VendedorDao createVendedorDao() {
		return new VendedorDaoJDBC(DB.getConnection());
	}
	
	public static DepartamentoDao createDepartamentoDao() {
		return new DepartamentoDaoJDBC(DB.getConnection());
	}
}
 