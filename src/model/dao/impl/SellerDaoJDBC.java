package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.table.Department;
import model.table.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection conn;

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement("INSERT INTO seller " + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES " + "(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, obj.getName());
			ps.setString(2, obj.getEmail());
			ps.setDate(3, obj.getBirthDate());
			ps.setDouble(4, obj.getBaseSalary());
			ps.setInt(5, obj.getDepartment().getId());

			int affectedRows = ps.executeUpdate();

			if (affectedRows > 0) {
				rs = ps.getGeneratedKeys();
				if (rs.next())
					obj.setId(rs.getInt(1));
				System.out.println("Success");
			} else
				throw new DbException("Unexpected error: No rows affected!");
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement("UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " + "WHERE Id = ?",
					Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, obj.getName());
			ps.setString(2, obj.getEmail());
			ps.setDate(3, obj.getBirthDate());
			ps.setDouble(4, obj.getBaseSalary());
			ps.setInt(5, obj.getDepartment().getId());
			ps.setInt(6, obj.getId());
			
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
			
			ps.setInt(1, id);
			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Seller s = new Seller();

		try {
			ps = conn.prepareStatement("SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?");
			
			ps.setInt(1, id);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				s.setId(rs.getInt("Id"));
				s.setName(rs.getString("Name"));
				s.setEmail(rs.getString("Email"));
				s.setBirthDate(rs.getDate("BirthDate"));
				s.setBaseSalary(rs.getDouble("BaseSalary"));
				
				Department dep = new Department();
				dep.setId(rs.getInt("DepartmentId"));
				dep.setName(rs.getString("DepName"));
				
				s.setDepartment(dep);
			}
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
		return s;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Seller> list = new ArrayList<Seller>();
		Map<Integer, Department> departments = new HashMap<Integer, Department>();

		try {
			ps = conn.prepareStatement("SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name");
						
			rs = ps.executeQuery();
			
			while (rs.next()) {
				Seller s = new Seller();
				
				s.setId(rs.getInt("Id"));
				s.setName(rs.getString("Name"));
				s.setEmail(rs.getString("Email"));
				s.setBirthDate(rs.getDate("BirthDate"));
				s.setBaseSalary(rs.getDouble("BaseSalary"));
				
				
				if (!departments.containsKey(rs.getInt("DepartmentId"))) {
					
					Department dep = new Department();
					dep.setId(rs.getInt("DepartmentId"));
					dep.setName(rs.getString("DepName"));
					
					departments.put(rs.getInt("DepartmentId"), dep);
				}
				
				
				s.setDepartment(departments.get(rs.getInt("DepartmentId")));
				
				list.add(s);
			}
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
		
		return list;
	}

	@Override
	public List<Seller> findByDepartment(Department d) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Seller> list = new ArrayList<Seller>();

		try {
			ps = conn.prepareStatement("SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name");
			
			ps.setInt(1, d.getId());
						
			rs = ps.executeQuery();
			
			while (rs.next()) {
				Seller s = new Seller();
				
				s.setId(rs.getInt("Id"));
				s.setName(rs.getString("Name"));
				s.setEmail(rs.getString("Email"));
				s.setBirthDate(rs.getDate("BirthDate"));
				s.setBaseSalary(rs.getDouble("BaseSalary"));
				d.setName(rs.getString("DepName"));
				s.setDepartment(d);
				
				list.add(s);
			}
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
		
		return list;
	}

}
