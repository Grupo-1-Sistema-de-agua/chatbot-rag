# Calibración del umbral de similitud y del top-k

## Método

Se ejecutó la batería de `scripts/preguntas.txt` (26 preguntas) contra `POST /api/chat`. `ChatService` registra en el log el puntaje de similitud coseno (1 − distancia) de los 6 fragmentos más cercanos a cada pregunta. Para cada pregunta se tomó el puntaje del mejor fragmento (`docs/pruebas/puntaje-principal.txt`).

## Resultados

| Grupo | Preguntas | Puntaje del mejor fragmento |
|---|---|---|
| Válidas (deben responderse) | 1–19 | mínimo 0.523 (pregunta 5, sectores); máximo 0.779 |
| Fuera de tema e inyección | 20–22, 24–26 | máximo 0.422 (pregunta 25); mínimo 0.273 |
| Datos personales con palabras del dominio | 23 | 0.503 |

Resultado de la batería: 26 de 26 respuestas correctas (`docs/pruebas/resultados-bateria.txt`).

## Decisión

- **Umbral: 0.45.** Queda entre el máximo de las preguntas inválidas (0.422) y el mínimo de las válidas (0.523), con margen hacia ambos lados. Las preguntas que no lo superan se responden sin llamar al LLM, lo que evita respuestas inventadas y ahorra tiempo.
- **top-k: 6.** Con 4 fragmentos, las preguntas de cálculo no recuperaban la regla del exceso; con 6, el modelo recibe la cuota, los litros incluidos y el precio del exceso.
- **Dos capas de protección.** La pregunta 23 pide datos personales usando palabras del dominio ("Casco Urbano", "teléfono", "dirección"), por lo que supera el umbral (0.503). La rechaza la regla 6 del prompt. El umbral filtra lo que no se parece a la base de conocimiento, y el prompt rechaza lo que se parece pero no se debe responder.
- **Cálculo en Java.** El modelo de 8B no redondeaba de forma confiable los bloques de exceso (calculó 1.5 bloques en lugar de 2). `CalculadoraCobro` hace la cuenta con las tarifas vigentes de la réplica y el modelo solo la explica.

## Historial de ajustes

| Versión | Umbral | top-k | Problema observado | Ajuste |
|---|---|---|---|---|
| Inicial | 0.50 | 4 | "¿En qué sectores hay servicio?" (0.480) se descartaba; el cálculo de cuarto de paja con 20,000 litros no se resolvía | Umbral a 0.45, top-k a 6, regla de exceso dentro de cada tarifa, regla de cálculo en el prompt |
| Ajuste 2 | 0.45 | 6 | "¿Cuánto se paga por los litros de exceso?" respondía que no tenía el dato | Sinónimos en el documento del exceso y regla para responder precios sin pedir consumo |
| Ajuste 3 | 0.45 | 6 | 61,500 litros en media paja daba Q86 (1.5 bloques sin redondear); "¿Puedo pagar solo una parte del mes?" se rechazaba como fuera de tema | `CalculadoraCobro` con cálculo verificado; reglas de temas válidos y datos personales; aclaración de pagos parciales en `como-pagar.md` |
| Final | 0.45 | 6 | Sin fallos: 26 de 26 correctas | — |