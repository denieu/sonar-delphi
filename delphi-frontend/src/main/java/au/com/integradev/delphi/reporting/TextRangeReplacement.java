/*
 * Sonar Delphi Plugin
 * Copyright (C) 2024 Integrated Application Development
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package au.com.integradev.delphi.reporting;

import org.sonar.plugins.communitydelphi.api.check.FilePosition;

public class TextRangeReplacement {
  private final FilePosition location;
  private final String replacement;

  public TextRangeReplacement(FilePosition location, String replacement) {
    this.location = location;
    this.replacement = replacement;
  }

  public FilePosition getLocation() {
    return location;
  }

  public String getReplacement() {
    return replacement;
  }
}
