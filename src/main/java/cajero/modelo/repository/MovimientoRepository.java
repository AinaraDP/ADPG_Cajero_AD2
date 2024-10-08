package cajero.modelo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cajero.modelo.entity.Movimiento;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer>{
	
	@Query("select m from Movimiento m where m.cantidad = ?1")
	public Movimiento movimientoPorCantidad(double cantidad);
	
	@Query("select m from Movimiento m where m.operacion =?1")
	public Movimiento movimientoPoroperacion(String operacion);
	
	
	@Query("select m from Movimiento m where m.cuenta.idCuenta =?1")
	public List<Movimiento>findByCuenta(int idCuenta);
	
}
