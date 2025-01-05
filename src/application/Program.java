package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import db.DB;
import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.dao.SellerDao;
import model.table.Department;
import model.table.Seller;

public class Program {

	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement st = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			conn = DB.getConnection();
			
			Seller seller = new Seller();
			
			seller.setName("Gabriel Brown Jr");
			seller.setEmail("gabriel@gmail.com");
			seller.setBirthDate(new java.sql.Date(sdf.parse("26/08/1997").getTime()));
			seller.setBaseSalary(5000.0);
			seller.setDepartment(new Department(2, null));
			seller.setId(9);
			
			SellerDao sellerDao = DaoFactory.createSellerDao(conn);
//			sellerDao.insert(seller);
//			sellerDao.update(seller);
//			sellerDao.deleteById(seller.getId());
//			sellerDao.findById(1);
			System.out.println(sellerDao.findAll());
//			System.out.println(sellerDao.findByDepartment(new Department(1, null)));
			
			DepartmentDao dd = DaoFactory.createDepartmentDao(conn);
//			dd.insert(new Department(null, "Marketing"));
//			dd.update(new Department(5, "Joias"));
//			dd.deleteById(5);
//			System.out.println(dd.findById(3));
			System.out.println(dd.findAll());
			
			
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			DB.closeStatement(st);
			DB.closeConnection();
		}
	}
}

