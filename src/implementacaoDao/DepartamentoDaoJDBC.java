package implementacaoDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dao.DepartamentoDao;
import db.DB;
import db.DbException;
import db.DbIntegrityException;
import entidades.Departamento;

public class DepartamentoDaoJDBC implements DepartamentoDao{

	private Connection conn;
	
	public DepartamentoDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Departamento departamento) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO departamento (Nome) VALUES (?)",
			Statement.RETURN_GENERATED_KEYS); // para retornar o Id do novo departamento inserido
			st.setString(1, departamento.getNome());
						
			int linhasAfetadas = st.executeUpdate();
			
			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();  // pega o Id gerado
				if (rs.next()) {
					int id = rs.getInt(1);
					departamento.setId(id);
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
	public void update(Departamento departamento) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE departamento SET Nome = ? WHERE Id = ? "); 
			st.setString(1, departamento.getNome());
			st.setInt(2, departamento.getId());
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
			st = conn.prepareStatement("DELETE FROM departamento WHERE id = ?"); 
			st.setInt(1, id);
			int linhasAfetadas = st.executeUpdate();
			if (linhasAfetadas == 0) {
				throw new DbException("Nenhuma exclusão ocorreu! Id inexistente");
			}
		}
		catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());  //Não podem ser deletados departamentos que tenham vendedores
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Departamento findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT departamento.* FROM departamento "
					+ " WHERE departamento.Id = ?");
			st.setInt(1,id);
			rs = st.executeQuery();
			if (rs.next()) {
				Departamento departamento = instanciaDepartamento(rs);
				return departamento;
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
	public List<Departamento> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT departamento.* FROM departamento ORDER BY Nome");
			
			rs = st.executeQuery();
			
			// criar uma lista para colocar todos os vendedores de um departamento
			List <Departamento> lista = new ArrayList<>();
			
			while (rs.next()) {
				Departamento departamento = instanciaDepartamento(rs);
				lista.add(departamento);
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
		departamento.setId(rs.getInt("Id"));
		departamento.setNome(rs.getNString("Nome"));
		return departamento;
	}
}
