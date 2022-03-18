import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Find_or_separate {

    public static int indexes_of_char(String str, char separator) {
        int[] arr = new int[4];
        for (int j = 0; j < arr.length; j++) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == separator) {
                    arr[j] = i;
                }
            }
        }

        return arr[2];
    }

    public static String string_handler(String line_for_changes, String separator) {

        //делим строку на части по пробелу и с лимитом 5 столбцов
        String[] arr_of_words = line_for_changes.split(" ", 4);

        //создаём новую строку со вставлением разделителя
        StringBuilder changed_line = new StringBuilder();
        for (String arr_of_word : arr_of_words) {

            changed_line.append(arr_of_word).append(separator);

        }

        //блок для правильного редактирования сообщений INFO и WARN
        if (changed_line.toString().contains("INFO" + separator + ": ")) {
            changed_line = new StringBuilder(changed_line.toString().replace("INFO" + separator + ": ",
                    "INFO:" + separator + separator));
        } else if (changed_line.toString().contains("WARN" + separator + ":")) {
            changed_line = new StringBuilder(changed_line.toString().replace("WARN" + separator + ": ",
                    "WARN:" + separator));
        }

        if (changed_line.toString().contains("\t")) {
            changed_line = new StringBuilder(changed_line.toString().replace("\t", separator));
        } else {
            changed_line = new StringBuilder(changed_line.substring(0, indexes_of_char(changed_line.toString(), separator.charAt(0))) +
                    separator + changed_line.substring(indexes_of_char(changed_line.toString(), separator.charAt(0)) + 1));
        }

        return changed_line.toString();

    }

    public static void separator(String separator, String path_to_file, String name_of_new) throws IOException {

        //считываем файлы в указанной папке
        List<Path> a = Files.list(Path.of(path_to_file)).collect(Collectors.toList());

        //создаём файл csv с указанным именем
        BufferedWriter writer_csv = new BufferedWriter(new FileWriter(path_to_file + "\\" + name_of_new + "_csv.csv"));

        //для каждого файла в списке
        for (Path path : a) {

            //читаем файл
            BufferedReader reader_source = new BufferedReader(new FileReader(String.valueOf(path)));

            //пока не кончились строки выполняем код
            String line_for_reading;
            while ((line_for_reading = reader_source.readLine()) != null) {

                String line_for_writing = string_handler(line_for_reading, separator);

                //
                writer_csv.append(String.valueOf(line_for_writing)).append("\n");

            }

            //
            reader_source.close();

        }

        //
        writer_csv.close();

    }

    public static void regex(String regex, String path_to_file, String name_of_new) throws IOException {

        //считываем файлы в указанной папке
        List<Path> a = Files.list(Path.of(path_to_file)).collect(Collectors.toList());

        //создаём файл csv с указанным именем
        BufferedWriter writer_regex = new BufferedWriter(new FileWriter(path_to_file + "\\" + name_of_new + "_regex.txt"));

        //для каждого файла в списке
        for (Path path : a) {

            //читаем файл
            BufferedReader reader_source = new BufferedReader(new FileReader(String.valueOf(path)));

            //пока не кончились строки выполняем код
            String line_for_reading;
            while ((line_for_reading = reader_source.readLine()) != null) {

                if (Pattern.matches(regex, line_for_reading)) {
                    writer_regex.append(line_for_reading).append("\n");
                }

            }

            //
            reader_source.close();

        }

        //
        writer_regex.close();

    }

    public static void main(String[] args) {

        System.out.println("FIND OR SEPARATE");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //читаем файл, проверяем его существование, получаем счётчик строк и путь к исходнику в переменные
        try {
            //получаем регулярку или разделитель
            String regex_or_separator = args[0];
//            System.out.print("Введите регулярное выражение для поиска или разделитель: ");
//            String regex_or_separator = reader.readLine();

            //получаем в переменную путь к исходному(ым) файлу(ам)
            String path_to_dir = args[1];
//            System.out.print("Введите путь к файлу(ам): ");
//            String path_to_dir = reader.readLine();

            //получаем имя нового файла, создаём его в той же директории, что и файлы логов
            String new_name = args[2];
//            System.out.print("Введите имя нового файла: ");
//            String new_name = reader.readLine();

            //разделение программы на два рукава
            switch (regex_or_separator) {
                case ",":
                case ";":
                case "    ":
                case "\t":
                    separator(regex_or_separator, path_to_dir, new_name);
                    break;
                default:
                    regex(regex_or_separator, path_to_dir, new_name);
                    break;
            }

            System.out.println("DONE");

        } catch (FileNotFoundException | NoSuchFileException e) {
            System.out.println("Файл с таким именем не найден. Попробуйте ещё раз");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UncheckedIOException e) {
            System.out.println("Неизвестный формат файла, попробуйте ещё раз");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Неверные параметры или их количество");
        }

    }

}

