// DNI 20521823 CASTRO VALERO, ALEJANDRO
import java.util.ArrayList;

public abstract class Habitante {
	
	private String nombre;
	private ArrayList<Producto> cesta;
	private double vigor;
	private char sexo;
	private static ArrayList<Habitante> poblacion = new ArrayList<Habitante>();
	
	public Habitante(String s,char c) {
		// nombre
		nombre = new String(s);
		// sexo
		if(c != 'M'  &&  c != 'H') sexo = 'H';
		else sexo = c;
		// vigor
		vigor = 100;
		// cesta
		cesta = new ArrayList<Producto>();
		// incluido dentro de poblacion
		poblacion.add(this);
	}
	
		
	public boolean recolecta(Terreno t,int i) {  // REVISADO p05678910 necesario p3
		boolean devuelve = false;
		Producto p = null;
		
		// comprobar i es un producto correcto y hay vigor suficiente
		if(t != null  &&  i != 5  &&  i < 7  &&  i > 0  &&  vigor > 0.0) {
			
			for(int x=0; x<t.getFilas(); x++)
				for(int y=0; y<t.getColumnas(); y++) {
					
					// si se cumplen las condiciones llamamos a recoge de t metemos el producto a cesta y decrementamos vigor
					if(t.consultaPeso(x, y) > 0.0  &&  t.consultaTipo(x, y) == i  &&  vigor > 0  &&  vigor >= t.consultaPeso(x, y) * 0.1) {  // ERROR CORREGIDO NUEVAS PRUEBAS ultimo condicional  
						p = t.recoge(x, y);
						cesta.add(p);
						p.setColocado(true);
						vigor -= 0.1 * cesta.get(cesta.size()-1).getPeso();  // ERROR CORREGIDO EN LAS NUEVAS PRUEBAS recogias el sitio de la parcela que estaba a null
						devuelve = true;
						x = t.getFilas();
						y = t.getColumnas();
					}
					else
						vigor -= 0.25;
					
					// el vigor del habitante no puede ser negativo
					if(vigor < 0) {
						vigor = 0;
						x = t.getFilas();
						y = t.getColumnas();
					}
				}
			
		}
		
		return devuelve;
	}
	
	
	public int estudio() {		// REVISADO p057
		ArrayList<Integer> menor = new ArrayList<Integer>();
		int producto = 1, repes = 0, aux = 0;
		int[] tipos = {1,2,3,4,6};
		
		if(cesta != null  &&  !cesta.isEmpty()) {
		
			// primero comprobamos cada tipo las veces que se repite en la cesta
			for(int i=0; i<tipos.length; i++) {
				for(int j=0; j<cesta.size(); j++)
					if(cesta.get(j).getTipo() == tipos[i])
						repes++;
				menor.add(repes);
				repes = 0;
				
				// aqui comprobamos si las repes del producto buscado ahora es menor que las del anterior
				if(i > 0  &&  menor.get(i-1) > menor.get(i))
					producto = tipos[i];
				else if(i > 0  &&  menor.get(i-1) <= menor.get(i)) {
					aux = menor.get(i);
					menor.set(i, menor.get(i-1));
					menor.set(i-1, aux);
				}
			}
			
		}
		
		return producto;
	}
	
	
	public double vigoriza(String s) {		// REVISADO p10 necesario p3
		int madera = -1;
		double incrementa = 0, falta = 100 - vigor;
		
		if(s != null  &&  cesta != null  &&  !cesta.isEmpty()) {
			
			// busca primera madera para no comertelo crudo
			for(int i=0; i<cesta.size(); i++)
				if(cesta.get(i) != null  &&  cesta.get(i).getTipo() == 4) {
					madera = i;
					break; // ERROR FINAL CORRECION faltaba esta sentencia
				}
			
			// buscamos un objeto de igual nombre a s y que sea vegetal o animal
			for(int i=0; i<cesta.size(); i++) {
				if(cesta.get(i) != null  &&  s.equalsIgnoreCase(cesta.get(i).getNombre())  &&  (cesta.get(i).getTipo() == 1  ||  cesta.get(i).getTipo() == 2)) {
					
					// cuando lo encontramos descolocamos el objeto y lo cocinamos o no con la madera para saber el valor
					cesta.get(i).setColocado(false);
					if(madera > -1) {
						incrementa = cesta.get(i).valorProducto();
						cesta.get(madera).setColocado(false);
						cesta.remove(madera);
					}
					else
						incrementa = 0.5 * cesta.get(i).valorProducto();
					cesta.remove(i);
				}
			}
			
			// incrementamos el vigor y devolvemos lo que falta
			vigor += incrementa;
			if(vigor < 100) falta = 100 - vigor;
			else {
				vigor = 100;
				falta = 0; // ERROR FINAL CORRECION faltaba esta sentencia
			}
			
		}
		
		return falta;
	}
	
	
	public Producto edifica(String s) {		// REVISADO p089
		Producto edificio = null;
		
		if(cesta != null  &&  !cesta.isEmpty()) { // ERROR VISUALIZADO se ha quitado el s distinto de null porque ya se trata en el constructor
		
			for(int i=0; i<cesta.size(); i++)
				if(cesta.get(i) != null  &&  cesta.get(i).getTipo() == 3) {
					edificio = new Producto(0.5*cesta.get(i).getPeso(),5,s);  // ERROR COREGIDO NUEVAS PRUEBAS habias puesto un 3 y no un 5
					cesta.get(i).setColocado(false);
					cesta.remove(i);
					break;													// ERROR CORREGIDO NUEVAS PRUEBAS no tenias el break
				}
			
		}
		
		return edificio;
	}
	
	
	public ArrayList<String> trueque(Habitante h) {  // REVISADO p05 necesario p3
		ArrayList<String> intercamb = null;
		Producto sobra = null, necesario = null;
		int max = 0, min = 0, pos = 0;
		
		if(h != null  &&  cesta != null  &&  !cesta.isEmpty()) {
			
			// averiguamos el primero producto de la cesta que mas predomina y lo cogemos
			max = this.estudioMayor();
			for(int i=0; i<cesta.size(); i++) {
				if(cesta.get(i) != null  &&  cesta.get(i).getTipo() == max) {
					sobra = cesta.get(i);
					sobra.setColocado(false);
					pos = i;
					break;
				}
			}
			
			// averiguamos el producto que mas necesita el habitante y pasamos un mensaje a h
			min = this.estudio();
			necesario = h.haceTrueque(min, sobra);
			
			// si nos ha devuelto un objeto eliminamos el que le hemos pasado y metemos el devuelto
			if(necesario != null) {
				cesta.remove(pos);
				cesta.add(necesario);
				necesario.setColocado(true);
				intercamb = new ArrayList<String>();
				intercamb.add(necesario.getNombre());
				intercamb.add(sobra.getNombre());
			}
			else sobra.setColocado(true);
		}
		
		return intercamb;
	}
	
	
	public Producto haceTrueque(int i,Producto p) {  // REVISADO p05 necesario p3
		Producto devuelve = null;
		
		if(p != null  &&  cesta != null  &&  !cesta.isEmpty()) {
			
			for(int x=0; x<cesta.size(); x++)
				if(cesta.get(x) != null  &&  cesta.get(x).getTipo() == i) {
					devuelve = cesta.set(x, p);
					p.setColocado(true);
					devuelve.setColocado(false);
					break;
				}
			
		}
		
		return devuelve;
	}
	
	
	public double tributa(Mistico m) {		// REVISADO p09 necesario p3
		double incremento = 0;
		ArrayList<Producto> entrega = new ArrayList<Producto>();
		
		if(m != null  &&  cesta != null  &&  !cesta.isEmpty()) {
			
			// rellenamos la lista de productos a entregar
			for(int x=1; x<7; x++)
				for(int i=0; i<cesta.size(); i++)
					if(cesta.get(i) != null  &&  cesta.get(i).getTipo() == x) {
						entrega.add(cesta.get(i));
						cesta.get(i).setColocado(false);
						cesta.remove(i);
						i = cesta.size();
					}
			
			// obtenemos el vigor y lo incrementamos
			incremento = m.culto(entrega, nombre);
			if((incremento+vigor) > 100) {
				incremento = 100 - vigor;
				vigor = 100;
			}
			else vigor += incremento;
		}
		
		return incremento;
	}
	
	
	public int plegaria(Mistico m,Terreno t) {		// REVISADO
		int recolectar = 0, tipo = 0;
		ArrayList<Integer> edificados = new ArrayList<Integer>();
		
		if(m != null  &&  t != null  &&  cesta != null  &&  !cesta.isEmpty()) {
			
			// primero averiguo el tipo de mayor escasez vegetal o animal
			for(int i=0; i<cesta.size(); i++) {
				if(cesta.get(i) != null) {
					if(cesta.get(i).getTipo() == 1) tipo++;
					if(cesta.get(i).getTipo() == 2) tipo--;
				}
			}
			if(tipo >= 0) tipo = 2;  // ERROR VISUALIZADO antes cogia el tipo mas abundante en la cesta
			else tipo = 1;
			
			// aqui nos quedamos con las posiciones que va a transformar el mistico
			for(int x=0; x<t.getFilas(); x++)
				for(int y=0; y<t.getColumnas(); y++) {
					
					// notemos que solo seran de tipo edificado segun la descripcion del metodo transforma
					if(t.consultaTipo(x, y) == 5)
						edificados.add((x*10) + y);
				}
				
			// invoco al metodo transforma para recibiendo el numero de productos transformados
			recolectar = m.transforma(t, tipo);
				
			// si el mistico ha transformado alguna parcela entonces recolectamos
			if(recolectar > 0) {
					
				// recolecta por coordenadas
				for(int i=0; i<edificados.size(); i++) {
					cesta.add(t.recoge(edificados.get(i)/10, edificados.get(i)%10));
					cesta.get(cesta.size()-1).setColocado(true);
				}
			}
		}
		
		return recolectar;
	}
	

	public String getNombre() {		// REVISADO
		return nombre;
	}
	
	
	public double getVigor() {		// REVISADO
		return vigor;
	}

	
	public String getClan() {		// REVISADO necesario p3
		String devuelve = null;
		
		if(nombre != null  &&  nombre.length() > 0) {
			
			String[] familia = nombre.split(" ");
			if(familia.length > 1  &&  familia[1] != null  &&  familia[1].length() > 0)
				devuelve = familia[1];

		}
		
		return devuelve;
	}
	
	
	public boolean perteneceClan(String s) {	// REVISADO necesario p3
		if(s != null  &&  s.equalsIgnoreCase(this.getClan()))
			return true;
		else
			return false;
	}
	
	
	public ArrayList<Producto> getCesta() {
		return cesta;
	}
	
	
	public static int getHombres() {
		int cont = 0;
		
		for(int i=0; i<poblacion.size(); i++) {
			if(poblacion.get(i).getSexo() == 'H')
				cont++;
		}
		
		return cont;
	}
	
	
	public static int getMujeres() {
		int cont = 0;
		
		for(int i=0; i<poblacion.size(); i++) {
			if(poblacion.get(i).getSexo() == 'M')
				cont++;
		}
		
		return cont;
	}
	
	
	public static ArrayList<Habitante> getPoblacion() {
		return poblacion;
	}
	
	
	// metodos adicionales
	
	public int estudioMayor() {  // REVISADO p05
		int tipo = 0, tipodef = 0, cont = 0, contdef = 0;
		
		// Primero contamos la cantidad de productos de un tipo
		for(int i=1; i<7; i++) {
			tipo = i;
			if(tipo != 5)
				for(int j=0; j<cesta.size(); j++)
					if(cesta.get(j) != null  &&  cesta.get(j).getTipo() == i)
						cont++;
			
			// despues vemos si hay mas cantidad que ese que el anterior guardado
			if(i == 1  ||  (i > 1  &&  contdef < cont)) {
				contdef = cont;
				tipodef = tipo;
			}
			cont = 0;
		}
		
		return tipodef;
	}
	
	
	public char getSexo() {
		return sexo;
	}
	
	
	// metodos abstractos de la practica 3
	
	
	public abstract boolean esAcogido(Clan c);
	public abstract boolean esDesterrado();
	public abstract void cambiaClan(String s);
	public abstract Clan getTribu();
	
	
	// metodos adicionales p3
	
	public int quitaCesta() { // vacia cesta y devuelve numero de productos
		int robados = 0;
		
		robados = cesta.size();
		cesta = new ArrayList<Producto>();
		
		return robados;
	}
	
	
	public int productosDelTipoMayor() { // devuelve la cantidad de productos de la cesta del tipo predominante
		int cantidad = 0, tipo = 0;
		
		tipo = this.estudioMayor();
		for(int i=0; i<cesta.size(); i++) {
			if(cesta.get(i).getTipo() == tipo)
				cantidad++;
		}
		
		return cantidad;
    }
	
	
	public Producto sacaProductoDeCesta(int p) { // saca el producto de cesta de la posicion
		Producto saca = null;
		
		if(cesta.get(p) != null) {
			cesta.get(p).setColocado(false);
			saca = cesta.remove(p);
		}
		
		return saca;
	}
	
	public ArrayList<String> truequePlebeyo(Habitante h, int p) { // realiza trueque segun los casos del metodo trueque de plebeyo pasando h para intercambiar y p para saber que producto
		ArrayList<String> intercambiados = null;
		Producto desecho = null, necesario = null;
		int tipo = 0;
		
		if(h != null  &&  cesta.size() > 0) {
			
			// sacamos el primer producto de la cesta
			desecho = cesta.get(p);
			desecho.setColocado(false);
			
			// y tambien el primer producto de tipo distinto a este
			for(int i=0; i<cesta.size(); i++)
				if(cesta.get(i).getTipo() != desecho.getTipo()) {
					tipo = cesta.get(i).getTipo();
					break;
				}
			
			// ahora hacemos haceTrueque y removemos y blablabla
			necesario = h.haceTrueque(tipo, desecho);
			
			if(necesario != null) {
				this.sacaProductoDeCesta(p);
				cesta.add(necesario);
				necesario.setColocado(true);
				intercambiados = new ArrayList<String>();
				intercambiados.add(necesario.getNombre());
				intercambiados.add(desecho.getNombre());
			}
			else
				desecho.setColocado(true);
			
		}
			
		return intercambiados;
	}
	
	
	public Producto getPrimeroCesta() { // devuelve el primer producto de la cesta
		Producto p = null;
		
		for(int i=0; i<cesta.size(); i++) {
			if(cesta.get(i) != null) {
				p = cesta.remove(i);
				p.setColocado(false);
				break;
			}
		}
		
		return p;
	}
	
	
	public double incrementaVigor(double v) { // devuelve la cantidad incrementada de vigor pero si decrementa devuelve cero
		double incrementado = 0.0;
		
		if(v > 0) {
			if((vigor + v) > 100) {
				incrementado = 100 - vigor;
				vigor = 100;
			}
			else {
				incrementado = v;
				vigor += v;
			}
		}
		else if(v < 0) {		// ERROR CORREGIDO habia puesto vigor en lugar de v
			if((vigor + v) < 0)
				vigor = 0;
			else
				vigor += v;
		}
		
		return incrementado;
	}
	
	
	public void almacena(Producto p) { // guarda el producto p en su cesta REVISADO p03
		if(p != null) {
			p.setColocado(true);
			cesta.add(p);
		}
	}
	
	
	public void setClan(String c) { // settea el apellido del habitante
		
		if(c != null) {
			String[] nom = nombre.split(" ");
			if(nom[0] != null  &&  nom[0].length() > 0)
				nombre = nom[0].concat(" ").concat(c);
		}
	}
	
	
	public Producto getPrimerFilosofal() { // devuelve el primer producto filosofal de la cesta
		Producto f = null;
		
		for(int i=0; i<cesta.size(); i++) {
			if(cesta.get(i) != null  &&  cesta.get(i).getTipo() == 6) {
				f = cesta.get(i);
				f.setColocado(false);
				break;
			}
		}
		
		return f;
	}
	
	
	public void alPrincipioDeCesta(ArrayList<Producto> b) { // coloca los productos pasados al principio de la cesta
		
		if(b != null  &&  b.size() > 0) {
			ArrayList<Producto> bag = b;
			
			// primero setteo colocado a true
			for(int i=0; i<bag.size(); i++)
				bag.get(i).setColocado(true);
			
			for(int i=0; i<cesta.size(); i++) {
				cesta.get(i).setColocado(true);
				bag.add(cesta.get(i));
			}
			cesta = bag;
		}
		
	}

	
}
