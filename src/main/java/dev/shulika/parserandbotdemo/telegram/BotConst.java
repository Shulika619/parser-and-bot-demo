package dev.shulika.parserandbotdemo.telegram;

public class BotConst {

    public static final String USERS_ACCESS = "usersAccess";
    public static final String START_COMMAND = "start";
    public static final String PRICE_COMMAND = "price";
    public static final String USER_ADD_COMMAND = "useradd";
    public static final String USER_DELETE_COMMAND = "userdelete";
    public static final String USER_LIST_COMMAND = "userlist";
    public static final String START_PARSE_COMMAND = "parse";

    public static final String START_MSG = """
            📃 *Команды:* 📃\n\n
            /price \\- *_Скачать файл с ценами_* \n\n
            ☝️ _Можете нажать на ссылку_\n
            ✏️ _Или написать и отправить команду_\n
            ⬇️ _Или нажать кнопку МЕНЮ и выбрать команду_\n
             """;
    public static final String DENIED_ACCESS = "⚠ *В доступе отказано* ⚠\n\n __Обратитесь к Администратору__ для получения прав\\!";
    public static final String SELECT_STORE = "*Выберите магазин* 🛒\n";
    public static final String NO_FILE = "⚠ *Файл отсутствует* ⚠\n";

}
