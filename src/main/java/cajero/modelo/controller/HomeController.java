package cajero.modelo.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cajero.modelo.dao.CuentaDao;
import cajero.modelo.dao.MovimientoDao;
import cajero.modelo.entity.Cuenta;
import cajero.modelo.entity.Movimiento;

import cajero.modelo.repository.MovimientoRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private CuentaDao cdao;
	@Autowired
	private MovimientoDao mdao;
	
	@Autowired
	private MovimientoRepository mrepo;
	
	
	/**
	 * Se encarga de las vistas a las rutas (""),("/") y ("/home")
	 * Si no hay ninguna cuenta con la sesion iniciada redirige al login
	 * 
	 * @param session, cuenta iniciada sesion
	 * @return formulario html con la pagina principal
	 */
	@GetMapping({"", "/", "/home"})
	public String home(HttpSession session) {
	    // Comprobar si hay una cuenta en la sesión
	    Cuenta cuenta = (Cuenta) session.getAttribute("cuenta");
	    
	    // Si no hay cuenta en la sesión, redirigir a la página de login
	    if (cuenta == null) {
	        return "redirect:/login";
	    }

	    // Si hay una cuenta, puedes redirigir a la página de inicio o mostrar el contenido de la cuenta
	    return "home"; // Esto carga la plantilla home.html si la sesión es válida
	}

	
	
	/**
	 * Se encarga de las vistas a la ruta ("/login")
	 * 
	 * @return formulario html con la pagina para logearse
	 */
	@GetMapping("/login")
	public String mostrarFormLogin() {
		
		return "formLogin";
	}
	
	/**
	 * Se encarga del proceso de inicio de sesion de la pagina, se le pasa un idCuenta y si coincide 
	 * con el id de una de las cuentas de la BBDD, deja iniciar sesion, si no, salta un mensaje
	 * de sesion no iniciada
	 * 
	 * 
	 * @param ratt, mensaje para decir que no se ha podido iniciar sesion
	 * @param sesion, la cuenta iniciada sesion
	 * @param idCuenta, id de la cuenta a la que estamos intentando acceder
	 * @return si no se ha podido iniciar sesion, redirige otra vez a la vista de "/login", si por 
	 * lo contrario si inicia sesion, nos devuelve al "/"
	 */
	@PostMapping("/login")
	public String procLogin(RedirectAttributes ratt, HttpSession sesion,
							@RequestParam int idCuenta) {
		Cuenta cuenta = cdao.login(idCuenta);
		
		if(cuenta != null) {
			sesion.setAttribute("cuenta", cuenta);
			return "redirect:/";
		}
		ratt.addFlashAttribute("mensaje", "cuenta incorrecta");
		return "redirect:/login";
	}
	
	/**
	 * Se encarga de las vistas a la ruta ("/logout")
	 * Mediante HttpSession borramos la cuenta de la sesion, por lo cual nos salimos y redirige
	 * a la vista de login
	 * 
	 * @param ratt, mensaje de sesion cerrada
	 * @return formulario html con la pagina para logearse
	 */
	@GetMapping("/logout")
	public String logout(RedirectAttributes ratt, HttpSession sesion) {
		
		sesion.removeAttribute("cuenta");
		sesion.invalidate();
		ratt.addFlashAttribute("mensaje", "Sesion cerrada");
		return "redirect:/login";
	}
	
	/**
	 * Se encarga de las vistas a la ruta ("/ingresar")
	 * 
	 * @return formulario html con la pagina para ingresar dinero
	 */
	@GetMapping("/ingresar")
	public String mostrFormIngresar() {
		
		return "formIngresar";
	}
	
	/**
	 * Se encarga del proceso de ingreso de dinero en la cuenta iniciada sesion.
	 * Accedemos a la cuenta, ingresamos dinero y modificamos la cuenta en la BBDD. Creamos un 
	 * nuevo movimiento donde se guardara la cantidad, la fecha y el tipo de movimiento.
	 * 
	 * 
	 * @param sesion, la cuenta iniciada sesion
	 * @param cantidad, cantidad que queremos ingresar
	 * @return cuando hacemos el ingreso redirige a la vista "/"
	 */
	@PostMapping("/ingresar")
	public String procFormIngresar( HttpSession sesion, @RequestParam double cantidad) {
		
		Cuenta cuenta = (Cuenta) sesion.getAttribute("cuenta");
		cuenta.ingresar(cantidad);
		cdao.modificar(cuenta);
		
		Movimiento mov = new Movimiento();
		
		

			mov.setFecha(new Date());
			mov.setOperacion("Ingreso");
			mov.setCantidad(cantidad);
			mov.setCuenta(cuenta);
			mdao.insertarMovimiento(mov);

			return "redirect:/";

	}
	
	/**
	 * Se encarga de las vistas a la ruta ("/extraer")
	 * 
	 * @return formulario html con la pagina para extraer dinero
	 */
	@GetMapping("/extraer")
	public String mostrFormExtraer() {
		
		return "formExtraer";
	}
	
	/**
	 * Se encarga del proceso de la extraccion de dinero de la cuenta iniciada sesion.
	 * Accedemos a la cuenta, si el saldo es menor a la cantidad que se intenta extraer, aparece
	 * un mensaje de error y nos redirige a la vista de extraccion en la que estabamos, si no,
	 * extrae el dinero de la cuenta y actualiza la BBDD y crea un movimiento con la cantidad,
	 * fecha y tipo de movimiento  
	 * 
	 * 
	 * @param ratt, mensaje para decir cantidad incorrecta
	 * @param sesion, la cuenta iniciada sesion
	 * @param cantidad, cantidad que queremos ingresar
	 * @return cuando hacemos la extraccion redirige a la vista "/", si no se hace la extraccion 
	 * redirige a la vista "/extraer"
	 */
	@PostMapping("/extraer")
	public String procFormExtraer(RedirectAttributes ratt, HttpSession sesion,
			@RequestParam double cantidad) {
		
		Cuenta cuenta = (Cuenta) sesion.getAttribute("cuenta");
		cdao.modificar(cuenta);
		
		Movimiento mov = new Movimiento();
		
		
		if(cuenta.getSaldo() >= cantidad) {
			cuenta.extraer(cantidad);
			mov.setFecha(new Date());
			mov.setOperacion("Extraccion");
			mov.setCantidad(cantidad);
			mov.setCuenta(cuenta);
			mdao.insertarMovimiento(mov);
			
			return "redirect:/";
			
		}
		ratt.addFlashAttribute("mensaje", "saldo negativo tras extraccion, indica una cantidad mayor o igual a " + cuenta.getSaldo());	
		return "redirect:/extraer";
	}
	
	/**
	 * Se encarga de las vistas a la ruta ("/transferencia")
	 * 
	 * @return formulario html con la pagina para hacer transferencia
	 */
	@GetMapping("/transferencia")
	public String mostrFormTransferencia() {
		
		return "formTransferencia";
	}
	
	/**
	 * Se encarga del proceso de la transferencia de la cuenta iniciada sesion a otra cuenta 
	 * Si el saldo a trasnferir es menor que la cantidad salta error y no deja hacer la transferencia
	 * Si no, se extrae el dinero de la cuenta origen y se ingresa en la cuenta de destino, tambien
	 * se modifican las cuentas en la BBDD.
	 * 
	 * @param ratt, mensaje para decir saldo negativo
	 * @param sesion, la cuenta iniciada sesion
	 * @param cantidad, cantidad que queremos ingresar
	 * @return cuando hacemos la extraccion redirige a la vista "/", si no se hace la extraccion 
	 * redirige a la vista "/extraer"
	 */
	@PostMapping("/transferencia")
	public String procFormTransferencia(RedirectAttributes ratt, HttpSession sesion,
			@RequestParam int idCuenta, @RequestParam double cantidad) {
		
		Cuenta cuentaO = (Cuenta) sesion.getAttribute("cuenta");
		Cuenta cuentaD = cdao.findById(idCuenta);
		
		Movimiento movO = new Movimiento();
		Movimiento movD = new Movimiento();
		
		if(cuentaO.getSaldo() < cantidad) {
			ratt.addFlashAttribute("mensaje", "saldo negativo, indica una cantidad menor o igual a " + cuentaO.getSaldo());	
			return "redirect:/transferencia";
		}
			cuentaO.extraer(cantidad);
			movO.setFecha(new Date());
			movO.setOperacion("extraccion por transferencia");
			movO.setCantidad(cantidad);
			movO.setCuenta(cuentaO);
			cdao.modificar(cuentaO);
			
			cuentaD.ingresar(cantidad);
			movD.setFecha(new Date());
			movD.setOperacion("ingreso por transferencia");
			movD.setCantidad(cantidad);
			movD.setCuenta(cuentaD);
			cdao.modificar(cuentaD);
			mdao.insertarMovimiento(movD);
			mdao.insertarMovimiento(movO);
			return "redirect:/";
	}
	
	
	/**
	 * Se encarga de las vistas a la ruta ("/movimientos/{idCuenta}")
	 * Crea una lista con todos los movimientos de la cuenta iniciada sesion
	 * 
	 * @return formulario html con la pagina ver la tabla de movimientos
	 */
	@GetMapping("/movimientos/{idCuenta}")
	public String mostrarFormMovimientos(@PathVariable int idCuenta, Model model) {
		List<Movimiento> movs = mrepo.findByCuenta(idCuenta);
		model.addAttribute("movimientos", movs);

		return "tablaMovimientos";
	}
	
	
	/**
	 * Metodo para convertir las cadenas de fecha en un objeto Date con un formato especifico
	 * @param binder para personalizar las fechas
	 */
	@InitBinder
    public void initBinder(WebDataBinder binder){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
	
	
}
