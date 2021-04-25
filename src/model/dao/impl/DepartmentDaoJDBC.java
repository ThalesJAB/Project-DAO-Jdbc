package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection conn;

	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department department) {
		PreparedStatement st = null;
		
		try {
			
			st = conn.prepareStatement("INSERT INTO department(Name)\r\n"
										+ "VALUES(?);", Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, department.getName());
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				
				if(rs.next()) {
					int id = rs.getInt(1);
					department.setId(id);
					DB.closeResultSet(rs);
				}
			
			}else {
				throw new DbException("Unexpected error! No rows affected!");
			}
			
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}

	}

	@Override
	public void update(Department department) {
		PreparedStatement st = null;
		
		try {
			
			st = conn.prepareStatement("UPDATE department "
										+ "SET Name = ? "
										+ "WHERE Id = ? ");
			
			st.setString(1, department.getName());
			st.setInt(2, department.getId());
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				System.out.println("Rows affected: " + rowsAffected);
			
			}else {
				throw new DbException("Unexpected error! No rows affected!");
			}
			
			
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		
		try {
			
			st = conn.prepareStatement("DELETE FROM department\r\n"
									+ "WHERE Id = ?");
			
			st.setInt(1, id);
			
			st.executeUpdate();
		
			
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}
		

	}

	@Override
	public Department findById(Integer id) {

		PreparedStatement st = null;
		ResultSet rs = null;
		
		
		try {
			st = conn.prepareStatement("SELECT * FROM Department "
					+ "WHERE Id =  ? ");
			
			
			st.setInt(1, id);
			
			rs = st.executeQuery();
			
			if(rs.next()) {
				Department dep = instantiateDepartment(rs);
				return dep;
				
			}
			
			return null;
			
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	
	}

	@Override
	public List<Department> findAll() {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT * FROM Department ");

			rs = st.executeQuery();

			List<Department> departments = new ArrayList<>();
			while (rs.next()) {
				Department dep = instantiateDepartment(rs);
				departments.add(dep);
			}

			return departments;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	}

	public Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("Id"));
		dep.setName(rs.getString("Name"));
		return dep;
	}

}