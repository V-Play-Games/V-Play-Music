/*
 * Copyright 2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vpg.bot;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.database.Database;
import net.vpg.bot.framework.Bot;

public class Driver {
    public static void main(String[] args) throws Exception {
        DataObject properties = DataObject.fromJson(Driver.class.getResourceAsStream("properties.json"));
        properties.put("token", System.getenv("TOKEN"));
        Bot bot = new Bot(properties);
        properties.getArray("managers").stream(DataArray::getLong).forEach(bot::addManager);
        bot.setDatabase(new Database(System.getenv("DB_URL"), "BotData", bot));
        bot.getShardManager().setPresence(OnlineStatus.INVISIBLE, null);
        bot.login();
    }
}
