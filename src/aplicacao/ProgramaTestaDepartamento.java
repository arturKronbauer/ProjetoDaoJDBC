package aplicacao;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

import dao.DepartamentoDao;
import entidades.Departamento;
import implementacaoDao.DaoFactory;

public class ProgramaTestaDepartamento {

	public static void main(String[] args) {
		
		// Cria uma injeção de dependência sem expor a implementação
		DepartamentoDao departamentoDao = DaoFactory.createDepartamentoDao();

		System.out.println("*** Teste 1 - departamento findByID ***");
		Departamento departamento = departamentoDao.findById(2);
		
		System.out.println(departamento);
		
		System.out.println("\n*** Teste 2 - departamento findAll ***");
		List<Departamento> lista = departamentoDao.findAll();
		for (Departamento obj : lista) {
			System.out.println(obj);
		}
		
		System.out.println("\n*** Teste 3 - departamento insert ***");
		Departamento novoDepartamento = new Departamento(null, "Bijuterias");
		departamentoDao.insert(novoDepartamento);
		System.out.println("Inserção ! Novo Id = "+novoDepartamento.getId());
		
		System.out.println("\n*** Teste 4 - departamento update ***");
		departamento = departamentoDao.findById(3);
		departamento.setNome("Cama&Mesa&Banho");
		departamentoDao.update(departamento);
		System.out.println("Atualização executada");
		
		System.out.println("\n*** Teste 5 - departamento delete ***");
		System.out.println("Informe o Id do departamento a ser excluido: ");
		Scanner sc = new Scanner(System.in);
		int id = sc.nextInt();
		departamentoDao.deleteById(id);
		System.out.println("Exclusão executada");
		sc.close();
	}
}
