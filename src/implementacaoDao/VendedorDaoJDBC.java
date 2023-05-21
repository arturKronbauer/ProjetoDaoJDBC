package implementacaoDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.VendedorDao;
import db.DB;
import db.DbException;
import entidades.Departamento;
import entidades.Vendedor;

public class VendedorDaoJDBC implements VendedorDao{

	private Connection conn;
	
	public VendedorDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Vendedor vendedor) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO vendedor "
			+ "(Nome, Email, DataNascimento, Salario, IdDepartamento) "
			+ "VALUES (?, ?, ?, ?, ?)",
			Statement.RETURN_GENERATED_KEYS); // para retornar o Id do novo vendedor inserido
			st.setString(1, vendedor.getNome());
			st.setString(2, vendedor.getEmail());
			st.setDate(3, new java.sql.Date(vendedor.getDataNascimento().getTime()));
			st.setDouble(4, vendedor.getSalario());
			st.setInt(5, vendedor.getDepartamento().getId());
			
			int linhasAfetadas = st.executeUpdate();
			
			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();  // pega o Id gerado
				if (rs.next()) {
					int id = rs.getInt(1);
					vendedor.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Erro inesperado! Nenhuma inserção ocorreu");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Vendedor vendedor) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE vendedor "
					+ "SET Nome = ?, Email = ?, DataNascimento = ?, Salario = ?, IdDepartamento = ? "
					+ "WHERE Id = ? "); 
			st.setString(1, vendedor.getNome());
			st.setString(2, vendedor.getEmail());
			st.setDate(3, new java.sql.Date(vendedor.getDataNascimento().getTime()));
			st.setDouble(4, vendedor.getSalario());
			st.setInt(5, vendedor.getDepartamento().getId());
			st.setInt(6, vendedor.getId());
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}	
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM vendedor "
					+ "WHERE id = ?"); 
			st.setInt(1, id);
			int linhasAfetadas = st.executeUpdate();
			if (linhasAfetadas == 0) {
				throw new DbException("Nenhuma exclusão ocorreu! Id inexistente");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Vendedor findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT vendedor.*, departamento.nome as DepNome "
					+ "FROM vendedor INNER JOIN departamento "
					+ "ON vendedor.IdDepartamento = departamento.Id "
					+ " WHERE vendedor.Id = ?");
			st.setInt(1,id);
			rs = st.executeQuery();
			if (rs.next()) {
				Departamento departamento = instanciaDepartamento(rs);
				Vendedor vendedor = instanciaVendedor(rs, departamento);
				return vendedor;
			}
		} 
		catch (SQLException e){
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		return null;
	}

	@Override
	public List<Vendedor> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT vendedor.*, departamento.Nome as DepNome "
					+ "FROM vendedor INNER JOIN departamento "
					+ "ON vendedor.IdDepartamento = departamento.Id "
					+ "ORDER BY Nome");
			
			rs = st.executeQuery();
			
			// criar uma lista para colocar todos os vendedores de um departamento
			List <Vendedor> lista = new ArrayList<>();
			
			// criar um Map vazio para guardar os departamentos instanciados
			Map<Integer, Departamento> map = new HashMap<>();
			
			while (rs.next()) {
				// procura no MAP se o departamento já existe
				Departamento dep = map.get(rs.getInt("IdDepartamento"));
				 
				// Se retornar null da pesquisa no MAP ai sim instancia o departamento
				if (dep == null) {
					// Chama o método para instanciar o departamento
					dep = instanciaDepartamento(rs);
					// Coloca o departamento no MAP para não coloca-lo novamente na próxima pesquisa
			        map.put(rs.getInt("IdDepartamento"), dep);   
				}
				
				Vendedor vendedor = instanciaVendedor(rs, dep);
				lista.add(vendedor);
			}
			return lista;
		} 
		catch (SQLException e){
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	public Departamento instanciaDepartamento(ResultSet rs) throws SQLException {
		Departamento departamento = new Departamento();
		departamento.setId(rs.getInt("IdDepartamento"));
		departamento.setNome(rs.getNString("DepNome"));
		return departamento;
	}
	
	public Vendedor instanciaVendedor(ResultSet rs, Departamento departamento) throws SQLException {
		Vendedor vendedor = new Vendedor();
		vendedor.setId(rs.getInt("Id"));
		vendedor.setNome(rs.getString("Nome"));
		vendedor.setEmail(rs.getString("Email"));
		vendedor.setDataNascimento(rs.getDate("DataNascimento"));
		vendedor.setSalario(rs.getDouble("Salario"));
		vendedor.setDepartamento(departamento);
		return vendedor;
	}

	@Override
	public List<Vendedor> findByDepartamento(Departamento departamento) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT vendedor.*, departamento.Nome as DepNome "
					+ "FROM vendedor INNER JOIN departamento "
					+ "ON vendedor.IdDepartamento = departamento.Id "
					+ "WHERE IdDepartamento = ? "
					+ "ORDER BY Nome");
			st.setInt(1,departamento.getId());
			rs = st.executeQuery();
			
			// criar uma lista para colocar todos os vendedores de um departamento
			List <Vendedor> lista = new ArrayList<>();
			
			// criar um Map vazio para guardar os departamentos instanciados
			Map<Integer, Departamento> map = new HashMap<>();
			
			while (rs.next()) {
				// procura no MAP se o departamento já existe
				Departamento dep = map.get(rs.getInt("IdDepartamento"));
				 
				// Se retornar null da pesquisa no MAP ai sim instancia o departamento
				if (dep == null) {
					// Chama o método para instanciar o departamento
					dep = instanciaDepartamento(rs);
					// Coloca o departamento no MAP para não coloca-lo novamente na próxima pesquisa
			        map.put(rs.getInt("IdDepartamento"), dep);   
				}
				
				Vendedor vendedor = instanciaVendedor(rs, dep);
				lista.add(vendedor);
			}
			return lista;
		} 
		catch (SQLException e){
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
}
