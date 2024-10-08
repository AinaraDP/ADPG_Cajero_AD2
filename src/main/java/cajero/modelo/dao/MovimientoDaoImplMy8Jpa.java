package cajero.modelo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cajero.modelo.entity.Cuenta;
import cajero.modelo.entity.Movimiento;
import cajero.modelo.repository.MovimientoRepository;

@Repository
public class MovimientoDaoImplMy8Jpa implements MovimientoDao {

	@Autowired
	private MovimientoRepository mrepo;
	
	@Override
	public Movimiento findById(int idMovimiento) {

		return mrepo.findById(idMovimiento).orElse(null);
	}

	
	@Override
	public Movimiento insertarMovimiento(Movimiento movimiento) {
		try {
			return mrepo.save(movimiento);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}


	@Override
	public List<Movimiento> findAll() {
		// TODO Auto-generated method stub
		return mrepo.findAll();
	}



	@Override
	public List<Movimiento> findByCuenta(int idCuenta) {
		
		return mrepo.findByCuenta(idCuenta);
	}
}
