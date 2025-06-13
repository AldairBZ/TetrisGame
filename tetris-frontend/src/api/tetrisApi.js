const API_URL = "http://localhost:8080/api"; // Cambia el puerto si tu backend usa otro

export async function getScore() {
  const response = await fetch(`${API_URL}/score`);
  return response.json();
}

export async function getRanking() {
  const response = await fetch(`${API_URL}/ranking`);
  return response.json();
}

// Agrega aquí más funciones según tu backend