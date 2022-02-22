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

import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.core.BotBuilder;
import net.vpg.bot.database.Database;

public class Driver {
    public static void main(String[] args) throws Exception {
        BotBuilder.createDefault("music", System.getenv("TOKEN"))
            .putProperties(DataObject.fromJson(Driver.class.getResourceAsStream("properties.json")))
            .setDatabase(new Database(System.getenv("DB_URL"), "BotData"))
            .build();
    }
}
