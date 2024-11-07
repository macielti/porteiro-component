# Porteiro Component

TBD

## Usage

### Migrations

The migrations files are located in the `resources/migrations` directory.

The migration configuration file is located in the `resources/migration.config.example.edn` file.

To run the migrations, use the following command:

```lein run -m pg.migration.cli -c resources/migration.config.example.edn migrate --all```

## License

Copyright © 2024 Bruno do Nascimento Maciel

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
