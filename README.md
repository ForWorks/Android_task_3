Создать приложение, на главной странице разместить ConstraintLayout, на нем следующие кнопки:

“Выбрать контакт”, по нажатию на нее открывается список контактов на устройстве (предварительно запросить доступ к ним). 
Выбранный из списка контакт сохраняется в базу данных (использовать Room, сохранять номер телефона, имя, фамилию, email). 
После сохранения отобразить Toast “Сохранение успешно”.

“Показать контакты”, по нажатию отобразить диалоговое окно со списком номеров телефонов, 
сохраненных в базе данных контактов, с возможностью кликнуть по одному из элементов. 
По клику диалоговое окно закрывается, а данные контакта отображаются в нижней части главной страницы, а номер телефона сохраняется в SharedPreferences.

“Показать из SP”, по нажатию на которую показывается (на все той же главной странице) сохраненный в SP номер телефона (если он там есть).

Для текстовых полей на форме использовать стили, по кнопке “Показать из SP” показать номер телефона не на форме, а в SnackBar, 
добавить еще одну кнопку “Показать в Notification”, по нажатию найти в базе данных контакт, номер телефона которого сохранен в SharedPreferences и 
отобразить имя и фамилию контакта в Notification.

![WmEdvHw7o8Q](https://user-images.githubusercontent.com/79687733/161838055-a3de0912-e047-468a-927a-926a2a743c78.jpg)
