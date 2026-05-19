package veronika.lab5.parsing;

import veronika.lab5.parsing.annotations.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class InterractiveObjectParser {
    public static Map<Class<?>, Function<String, ?>> parserMap = new HashMap<>();
//    В мэпе в качестве ключа принимаем какой-то неизвестный класс, а в качестве значения
//    объект функционального интерфейса


    //    заполняем мэпу, в качестве значения: преобразуем тип стринг в другие
    static {
        parserMap.put(String.class, str -> String.valueOf(str));
        parserMap.put(byte.class, str -> Byte.parseByte(str));
        parserMap.put(short.class, str -> Short.parseShort(str));
        parserMap.put(int.class, str -> Integer.parseInt(str));
        parserMap.put(long.class, str -> Long.parseLong(str));
        parserMap.put(float.class, str -> Float.parseFloat(str));
        parserMap.put(double.class, str -> Double.parseDouble(str));
        parserMap.put(char.class, str -> str.charAt(0));
//        т.к. нет функции для char, мы используем метод charAt, который
//        просто берёт 0 символ
        parserMap.put(boolean.class, str -> Boolean.parseBoolean(str));
        parserMap.put(Byte.class, str -> Byte.parseByte(str));
        parserMap.put(Short.class, str -> Short.parseShort(str));
        parserMap.put(Integer.class, str -> Integer.parseInt(str));
        parserMap.put(Long.class, str -> Long.parseLong(str));
        parserMap.put(Float.class, str -> Float.parseFloat(str));
        parserMap.put(Double.class, str -> Double.parseDouble(str));
        parserMap.put(Character.class, str -> str.charAt(0));
        parserMap.put(Boolean.class, str -> Boolean.parseBoolean(str));
        parserMap.put(LocalDateTime.class, (string) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            return LocalDateTime.parse(string, formatter);
        });
    }

    public static <T> T createObj(Class<T> clazz, Scanner scanner) {
        Function<String, ?> function = parserMap.get(clazz);
//        функциональный интерфейс Function принимает один тип данных, а возвращает какой-то другой
        if (function != null) {
            String result = scanner.nextLine();
            if (result.isEmpty()) {
                if (clazz.isPrimitive()) {
                    throw new NullPointerException();
//                    выбрасываем эксепшн т.к. примитичный класс нельзя заполнять значениями null
                }
                return null;
            }
            return (T) function.apply(result);
        }
        if (clazz.isEnum()) {
            System.out.print(Arrays.toString(clazz.getEnumConstants()) + ": ");
            String str = scanner.nextLine();
            if (str.isEmpty()) {
                return null;
            } else {
                for (T object : clazz.getEnumConstants()) {
                    Enum<?> element = (Enum<?>) object;
                    if (element.name().equals(str)) {
                        return object;
                    }
                }
                throw new RuntimeException("no enum constant with name " + str + " was found");
            }
        }

        try {
            Constructor<T> constructor = clazz.getConstructor();
            T obj = constructor.newInstance();
//            вызвали пустой конструктор и на конструкторе создали объект (допустим для класса Ticket)
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(IgnoreInput.class)) {
                    continue;
                }
                if (field.isAnnotationPresent(DescriptionAndValidate.class)) {
                    DescriptionAndValidate annotation = field.getAnnotation(DescriptionAndValidate.class);
                    String nameAnnotation = annotation.name();
                    String validationAnnotation = annotation.validation();
                    if (validationAnnotation.isEmpty()) {
                        System.out.print("Введите " + nameAnnotation + ": ");
                    } else {
                        System.out.print("Введите " + nameAnnotation + " (" + validationAnnotation + ") " + ": ");
                    }
                }
                Object value; //переменная без значения
                do {
//                    здесь бесконечнный цикл, нужен для того, чтобы пользователь ввел корректные данные
//                    завершится когда пользователь введет корректные данные
                    try {
                        value = createObj(field.getType(), scanner);
//                        рекурсивная инициализация переменной
                        validate(field, value);
                        break;
                    } catch (Exception exception) {
                        System.out.print("Введите пж корректные данные: ");
//                        обрабатываются все эксепшены, которые  у нас в методе validate
                    }
                } while (true);


                field.set(obj, value);
            }
            return obj;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static void validate(Field field, Object value) {
        if (value == null && field.isAnnotationPresent(NoNull.class)) {
            throw new NullPointerException();
        }
        if (value != null && field.isAnnotationPresent(BiggerThan.class)) {
            BiggerThan annotation = field.getAnnotation(BiggerThan.class);
            Number number = (Number) value;
            if (number.longValue() <= annotation.value()) {
                throw new IllegalArgumentException();

            }
        }
        if (value != null && field.isAnnotationPresent(SmallerThan.class)) {
            SmallerThan annotation = field.getAnnotation(SmallerThan.class);
            Number number = (Number) value;
            if (number.longValue() > annotation.value()) {
                throw new IllegalArgumentException();
            }
        }
        if (value != null && field.isAnnotationPresent(LengthBiggerThan.class)) {
            LengthBiggerThan annotation = field.getAnnotation(LengthBiggerThan.class);
            String string = (String) value;
            if (string.length() > annotation.value()) {
//                провервяем ввел ли пользователь длину строки больше чем наша аннотация, если да выбрасываем эксепшн
                throw new IllegalArgumentException();
            }
        }
    }

    public static String printOnbject(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> clazz = object.getClass();
        if (clazz.isPrimitive() || clazz.isEnum() || Number.class.isAssignableFrom(clazz) || clazz == Boolean.class || clazz == String.class || Temporal.class.isAssignableFrom(clazz)) {
            return String.valueOf(object);
        }
        Field[] fields = clazz.getDeclaredFields();
        String out = "";
        for (Field field : fields
        ) {
            try {
                field.setAccessible(true);
                Object value = field.get(object);
                out += humanNameField(field) + ": " + printOnbject(value) + "\n";
            } catch (ReflectiveOperationException e) {
                out += field.getName() + ": не удалось получить информацию из-за ошибки " + e;
            }
        }
        return out;
    }

    private static String humanNameField(Field field) {
        if (field.isAnnotationPresent(DescriptionAndValidate.class)) {
            DescriptionAndValidate annotation = field.getAnnotation(DescriptionAndValidate.class);
            return annotation.name();
        } else return field.getName();
    }
}
