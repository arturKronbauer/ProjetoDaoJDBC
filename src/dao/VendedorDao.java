package dao;

import java.util.List;

import entidades.Departamento;
import entidades.Vendedor;

public interface VendedorDao {
	void insert(Vendedor obj);
	void update(Vendedor obj);
	void deleteById(Integer id);
	Vendedor findById(Integer id);
	List <Vendedor> findAll();
	List <Vendedor> findByDepartamento(Departamento departamento);
}
