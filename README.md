Консольное приложение, которое по заданным параметрам генерирует файл с изображением фрактала Жулиа (https://en.wikipedia.org/wiki/Julia_set).
Пример:

java JuliaFractalGenerator -d "768;768" -c "-0.4;0.6" -o "julia_fractal.png"

Параметры командой строки:
-d  размер итогового изображения как:  Width ; Height
-c  значение постоянного слагаемоего как:  Real-part ; Imaginary-part
-o  путь до файла для сохранения (формат файла можно определять по расширению указанного файла)

Для обеспечения эффективной работы приложения расчет точек изображения фрактала делать в многопоточном режиме, используя все ресурсы процессора.

![6](https://github.com/AirSunday/JuilaSet/assets/42736248/8a20fa17-ca3b-4b23-b787-d3b2bcaeaa08)
