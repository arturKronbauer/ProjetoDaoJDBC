package aplicacao;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

import dao.VendedorDao;
import entidades.Departamento;
import entidades.Vendedor;
import implementacaoDao.DaoFactory;

public class ProgramaTestaVendedor {

	public static void main(String[] args) {
		
		// Cria uma injeção de dependência sem expor a implementação
		VendedorDao vendedorDao = DaoFactory.createVendedorDao();

		System.out.println("*** Teste 1 - vendedor findByID ***");
		Vendedor vendedor = vendedorDao.findById(3);
		
		System.out.println(vendedor);
		
		System.out.println("\n*** Teste 2 - vendedor findByDepartamento ***");
		Departamento departamento = new Departamento(2,null);
		List<Vendedor> lista = vendedorDao.findByDepartamento(departamento);
		for (Vendedor obj : lista) {
			System.out.println(obj);
		}
		
		System.out.println("\n*** Teste 3 - vendedor findAll ***");
		lista = vendedorDao.findAll();
		for (Vendedor obj : lista) {
			System.out.println(obj);
		}
		
		System.out.println("\n*** Teste 4 - vendedor insert ***");
		Vendedor novoVendedor = new Vendedor(null, "Renata", "renata@gmail.com", new Date(), 5000.0, departamento);
		vendedorDao.insert(novoVendedor);
		System.out.println("Inserção ! Novo Id = "+novoVendedor.getId());
		
		System.out.println("\n*** Teste 5 - vendedor update ***");
		vendedor = vendedorDao.findById(9);
		vendedor.setNome("Tamires Cardoso");
		vendedorDao.update(vendedor);
		System.out.println("Atualização executada");
		
		System.out.println("\n*** Teste 6 - vendedor delete ***");
		System.out.println("Informe o Id do vendedor a ser excluido: ");
		Scanner sc = new Scanner(System.in);
		int id = sc.nextInt();
		vendedorDao.deleteById(id);
		System.out.println("Exclusão executada");
		sc.close();
	}
}
